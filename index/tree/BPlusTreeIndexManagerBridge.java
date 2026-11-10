import java.util.Optional;
import java.util.logging.Logger;

private class BPlusTreeIndexManagerBridge <K extends Comparable<K>, V extends Number & Comparable<V>> implements DuplicateQueryableIndex<K, v>{
    private static final Logger Logger = LoggerFactory.getLogger(BPlusTreeIndexManagerBridge.class);
    private static final String EM_POINTER_POINTS_TO_NO_DATA = "while adding index key {} and value {} to index the pointer offered by index manager points to No Data";

    private static final int SCHEME_ID = -1;
    private final int collectionID;
    private final UniqueQueryableIndex<K, Pointer> indexManager;
    private final IndexBinaryObjectFactory<V> valueIndexBinaryObjectFactory;
    private final DatabaseStorageManager databaseStorageManager;
    private final EngineConfig engineConfig;

    public BPlusTreeIndexManagerBridge(int collectionID, EngineConfig engineConfig, UniqueQueryableIndex<K, Pointer> indexManager, IndexBinaryObjectFactory valueIndexBinaryObjectFactory, DatabaseStorageManager databaseStorageManager){
        this.collectionID = collectionID;
        this.engineConfig = engineConfig;
        this.indexManager = indexManager;
        this.valueIndexBinaryObjectFactory = valueIndexBinaryObjectFactory;
        this.databaseStorageManager = databaseStorageManager;
    }

    private Optional<BinaryList<V>> getBinaryList(K identifier) throws InternalOperationException{
        Optional<Pointer> pointerOptional = this.indexManager.getIndex(identifier);
        if(pointerOptional.isEmpty()){
            return Optional.empty();
        }

        Pointer pointer = pointerOptional.get();
        Optional<DBObject> dbObjectOptional = databaseStorageManager.select(pointer);
        return dbObjectOptional.map(dbObject -> new BinaryList<>(engineConfig, valueIndexBinaryObjectFactory, dbObject.getData()));
    }
}