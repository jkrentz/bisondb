package bisondb.jcascalog;

import bisondb.cascading.BisonDBTap;
import bisondb.DomainSpec;
import cascalog.Util;
import clojure.lang.*;

public class BDB {
  public static Object makeKeyValTap(String path) {
    return makeKeyValTap(path, null);
  }

  public static Object makeKeyValTap(String path, DomainSpec spec) {
    return makeKeyValTap(path, spec, new BisonDBTap.Args());
  }

  public static Object makeKeyValTap(String path, DomainSpec spec, BisonDBTap.Args args) {
    if(args==null) args = new BisonDBTap.Args();
    IFn keyvalfn = Util.bootSimpleFn("bisondb.cascalog.keyval", "keyval-tap");
    try {
      return keyvalfn.invoke(path,
                             kw("spec"), spec,
                             kw("tmp-dirs"), args.tmpDirs,
                             kw("source-fields"), args.sourceFields,
                             kw("version"), args.version,
                             kw("timeout-ms"), args.timeoutMs
                             );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Keyword kw(String kw) {
    return Keyword.intern(kw);
  }
}
