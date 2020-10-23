package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;


interface DataInteface {

    void createTable();
    void deleteSQL();
    void deleteCache();
    void deleteAll();
    int getCacheCount();

    void forceCacheToSQL();
}
