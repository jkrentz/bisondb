package bisondb.index;

import bisondb.persistence.Persistence;

import java.io.IOException;
import java.io.Serializable;

public interface Indexer<P extends Persistence, D> extends Serializable {
    public void index(P persistence, D doc) throws IOException;
}
