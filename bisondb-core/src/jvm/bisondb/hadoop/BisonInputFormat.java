package bisondb.hadoop;

import bisondb.DomainSpec;
import bisondb.Utils;
import bisondb.persistence.CloseableIterator;
import bisondb.persistence.Persistence;
import bisondb.document.KeyValDocument;
import bisondb.store.DomainStore;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapred.*;
import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BisonInputFormat implements InputFormat<NullWritable, BisonRecordWritable> {
    public static Logger LOG = Logger.getLogger(LocalBisonManager.class);
    public static final String ARGS_CONF = "bison.input.args";

    public static class Args implements Serializable {
        public String inputDirHdfs;
        public Long version = null;

        public Args(String inputDirHdfs) {
            this.inputDirHdfs = inputDirHdfs;
        }
    }

    public static class BisonRecordReader implements RecordReader<NullWritable, BisonRecordWritable> {
        BisonInputSplit split;
        Reporter reporter;
        Args args;
        LocalBisonManager bisonManager;
        Persistence lp;
        CloseableIterator iterator;
        boolean finished = false;
        int numRead = 0;
        boolean hasShard = false;

        public BisonRecordReader(BisonInputSplit split, Reporter reporter)
                throws IOException {
            this.split = split;
            this.reporter = reporter;
            args = (Args) Utils.getObject(this.split.conf, ARGS_CONF);
            bisonManager = new LocalBisonManager(
                    Utils.getFS(this.split.shardPath, split.conf), this.split.spec,
                    LocalBisonManager.getTmpDirs(this.split.conf), this.reporter);
        }

        public boolean next(NullWritable k, BisonRecordWritable v) throws IOException {
            if (!hasShard) {
                LOG.info("Downloading remote shard at " + split.shardPath);
                String localpath = bisonManager.downloadRemoteShard("shard", split.shardPath);

                Map<String, Object> opts = split.spec.getPersistenceOptions();
                lp = split.spec.getCoordinator().openPersistenceForRead(localpath, opts);

                iterator = lp.iterator();
                hasShard = true;
            }
                
            if (iterator.hasNext()) {
                Object document = iterator.next();
                KeyValDocument doc = (KeyValDocument) document;

                v.key = doc.key;
                v.value = doc.value;

                numRead++;
                if (reporter != null) {
                    reporter.progress();
                }

                return true;
            } else {
                if (reporter != null) { reporter.progress(); }
                return false;
            }
        }

        public NullWritable createKey() {
            return NullWritable.get();
        }

        public BisonRecordWritable createValue() {
            return new BisonRecordWritable();
        }

        public long getPos() throws IOException {
            // TODO: switch this to use a property on the iterator.
            return numRead;
        }

        public void close() throws IOException {
            iterator.close();
            lp.close();
            bisonManager.cleanup();
        }

        public float getProgress() throws IOException {
            if (finished) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * PROBLEMS HERE: We have to have some way of generating more than one split for every
     * shard.
     */
    public static class BisonInputSplit implements InputSplit {
        public static Logger LOG = Logger.getLogger(BisonInputSplit.class);

        private String shardPath;
        private DomainSpec spec;
        private JobConf conf;

        public BisonInputSplit() {
        }

        public BisonInputSplit(String shardPath, DomainSpec spec, JobConf conf) {
            this.shardPath = shardPath;
            this.spec = spec;
            this.conf = conf;
        }
        
        // TODO: Store this result in a variable and use it to update the
        // percentage complete.
        public long getLength() throws IOException {
            FileSystem fs = Utils.getFS(shardPath, conf);
            return fs.getContentSummary(new Path(shardPath)).getLength();
        }

        public String[] getLocations() throws IOException {
            // TODO: look at a file in the shardpath
            return new String[]{};
        }

        public void write(DataOutput d) throws IOException {
            spec.write(d);
            WritableUtils.writeString(d, shardPath);
            conf.write(d);
        }

        public void readFields(DataInput di) throws IOException {
            spec = new DomainSpec();
            spec.readFields(di);
            shardPath = WritableUtils.readString(di);
            conf = new JobConf();
            conf.readFields(di);
        }
    }


    public InputSplit[] getSplits(JobConf jc, int ignored) throws IOException {
        Args args = (Args) Utils.getObject(jc, ARGS_CONF);
        FileSystem fs = Utils.getFS(args.inputDirHdfs, jc);
        DomainStore store = new DomainStore(fs, args.inputDirHdfs);
        String versionPath;
        if (args.version == null) {
            versionPath = store.mostRecentVersionPath();
        } else {
            versionPath = store.versionPath(args.version);
        }
        DomainSpec spec = store.getSpec();
        List<InputSplit> ret = new ArrayList<InputSplit>();
        for (int i = 0; i < spec.getNumShards(); i++) {
            String shardPath = versionPath + "/" + i;
            if (fs.exists(new Path(shardPath))) {
                ret.add(new BisonInputSplit(new Path(shardPath).makeQualified(fs).toString(), spec, jc));
            }
        }
        return ret.toArray(new InputSplit[ret.size()]);
    }


    public RecordReader<NullWritable, BisonRecordWritable> getRecordReader(InputSplit is, JobConf jc,
                                                                     Reporter reporter) throws IOException {
        return new BisonRecordReader((BisonInputSplit) is, reporter);
    }
}
