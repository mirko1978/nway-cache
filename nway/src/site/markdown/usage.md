Cache Usage
============

### Example
    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
        .build(key -> {return myDataDAO.load(key)});

### Applicability
Caches are tremendously useful in a wide variety of use cases. For org.mirko.cache.example, you should consider using caches 
when a value is expensive to compute or retrieve, and you will need its value on a certain input more than once.

A Cache is similar to `ConcurrentMap`, but not quite the same. The most fundamental difference is that a 
`ConcurrentMap` persists all elements that are added to it until they are explicitly removed. A Cache on 
the other hand is generally configured to evict entries automatically, in order to constrain its memory 
footprint. In some cases a `Cache` can be useful even if it doesn't evict entries, due to its automatic cache 
loading.

Generally, the n-way caching is applicable whenever:

* You are willing to spend some memory to improve speed.
* You expect that keys will sometimes get queried more than once.
* Your cache will not need to store more data than what would fit in RAM. (Cache is local to a single run of your 
application. It is not storing data in files, or on outside servers.)
If each of these apply to your use case, then the N-Way cache could be right for you!

Obtaining a Cache is done using the `CacheBuilder` builder pattern as demonstrated by the org.mirko.cache.example code above, but 
customizing your cache is the interesting part.

### Population
The first question to ask yourself about your cache is: is there some sensible default function to load or 
compute a value associated with a key? If so, you should use a `CacheLoader`. 
Elements can be inserted directly, using `Cache.put`, but automatic cache loading is preferred as it makes it 
easier to reason about consistency across all cached content.

#### From a CacheLoader
Creating a `CacheLoader` is typically as 
easy as implementing the method `Value load(Key key) throws Exception`. So, for org.mirko.cache.example, you could create a 
`Cache` with the following code:

    Cache<Key, Graph> myCache = new NWayCacheBuilder<>()
        .build(
            new CacheLoader<Key, Graph>() {
                public Graph load(Key key) throws Exception {
                    return createExpensiveGraph(key);
                }
       });     
    ...
    try {
        return graphs.get(key);
    } catch (Exception e) {
        throw new OtherException(e.getCause());
    }

The canonical way to query a `Cache` is with the method `get(K)`. This will either return an already 
cached value, or else use the cache's `CacheLoader` to atomically load a new value into the cache. Because 
`CacheLoader` might throw an `Exception`, `Cache.get(K) throws Exception`. 

#### Inserted Directly
Values may be inserted into the cache directly with `Cache.put(key, value)`. This overwrites any previous 
entry in the cache for the specified key. 


### Eviction
The cold hard reality is that we almost certainly don't have enough memory to cache everything we could cache. 
You must decide: when is it not worth keeping a cache entry? 
N-Way cache provides three algorithms to do so: LRU, MRU, LRU Expired. In addition it is possible to write your
own eviction algorithm.

The eviction algorithm is not going to physically delete any cache entry. The deletion process is managed by the
 cache implementation itself. Instead the eviction is marking the entries as `DELETED`.
 
Each cache block contains the entries in creation order (the older are first), this is guarantee by the Cache implementation.

#### LRU Algorithm
The class `LRUAlgorithm` implements of a simple version of [LRU algorithm](http://en.wikipedia.org/wiki/Cache_algorithms#LRU).

This implementation deletes only the oldest `LRUAlgorithm.entriesToDelete` entries from the current block.
 
For org.mirko.cache.example:  
Remember that each cache block contains the entries in creation order (the older are first)
  
    Memory block = [ 1 -> "first", 5 -> "apple", 2 -> "red", 10 -> "table", 3-> "orange" ]
    entriesToDelete = 3
    
After the eviction:

    Memory block = [ 10 -> "table", 3-> "orange" ]

Usage:

    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
        .LRUEviction() // Or nothing because it is the default
    .build(key -> ... });

#### MRU Algorithm
The class `MRUAlgorithm` implements of a simple version of [MRU algorithm](http://en.wikipedia.org/wiki/Cache_algorithms#LRU).

This implementation deletes only the latest `LMRUAlgorithm.entriesToDelete` entries from the current block.  
In other words it is just the opposite than LRU.

For org.mirko.cache.example:  
Remember that each cache block contains the entries in creation order (the older are first)
  
    Memory block = [ 1 -> "first", 5 -> "apple", 2 -> "red", 10 -> "table", 3-> "orange" ]
    entriesToDelete = 3
    
After the eviction:

    Memory block = [ 1 -> "first", 5 -> "apple" ]

Usage:

    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
        .MRUEviction() 
    .build(key -> ... });
        
#### LRU Expired Algorithm
The class `LRUExpiredAlgorithm` implements a LRU algorithm based on invalidating the entries not used for a while.  
The differences from simple LRU are:

* Eviction is based on the expired entries (time based). An entry is expired when the access time is too old.  
 The time is expressed in milliseconds.
* Guarantee at least one eviction. If no element are expired the oldest one is deleted.

Eviction entry calculation:

    Expiration Time = Entry Access time + expiration   
    if Expiration Time < Current time then  
        mark for deletion current entry        
 
For org.mirko.cache.example:
  
    Memory block = [ 1 -> ("first", AccessTime: 1000) , 5 -> ("apple", AccessTime: 11000), 
                     2 -> ("red", ("apple", AccessTime: 10010), 10 -> ("table", AccessTime: 100), 
                     3-> ("orange",  AccessTime: 10005) ]
    expiration = 5000
    currentTime = 12000
    
After the eviction:

    Memory block = [ 5 -> ("apple", AccessTime: 11000),  2 -> ("red", ("apple", AccessTime: 10010)]

Usage:

    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
        .LRUExpiredEviction() 
    .build(key -> ... });    
    
#### Custom Algorithm
Creating a custom eviction algorithm is typically as easy as implementing 
`CacheEviction.eviction(List<CacheEntry<Key, Value>> block)`.
The follow point are to take in consideration:

* Each cache block contains the entries in creation order (the older are first), this is guarantee by the Cache implementation.
* The block is immutable. It is not possible to add or delete entries.
* `CacheEntry.status` is used to determine if the entry has to be deleted or not (DELETED, ACTIVE)

Usage:

    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
        .customEviction(block -> your beautiful eviction algorithm) 
    .build(key -> ... });    
    
### Listeners
Three different types of listeners are provided in order to take actions or collect cache information: removal, cached, miss.  
Is it possible to add and create more than one listener per type. The cache implementation guarantee to call all
of them.

#### Removal Listeners

`RemovalListener` is called after removing an entry from the cache.

    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
              .build(key -> {return ...)});
              
    RemovalListener<Key, DatabaseConnection> removalListener = new RemovalListener<Key, DatabaseConnection>() {
        public void onRemoval(RemovalNotification<Key, DatabaseConnection> removal) {
            DatabaseConnection conn = removal.getValue();
            conn.close(); // tear down properly
        }
    };
            
    myCache.addRemovalListener(removalListener);
  
#### Miss Listener
`MissListener` is called when a entry is requested and it is not found in the cache (miss).
  
    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
              .build(key -> {return ...)});
              
    MissListener<Key> missListener = new MissListener<Key>() {
        public void onMiss(Key) {
            collectMissStatistics();
        }
    };
            
    myCache.addMissListener(missListener);

#### Cached Listener
`CachedListener` is called when a entry is requested and it is in the cache. No load is necessary for retrieving the value.  
Please note that this listener can slow down the cache performance. It is strongly suggested to implement the method in 
a separate thread.
  
    Cache<Integer, String> myCache = new NWayCacheBuilder<>()
              .build(key -> {return ...)});
              
    CachedListener<Key> cachedListener = new MissListener<Key>() {
        public void onCache(CacheNotification<Key, Value> notification) {
            collectCacheStatistics(notification);
        }
    };
            
    myCache.addCachedListener(cachedListener);
   
### Appendix: Usage class diagram

![Public usage class diagram](client_usage_diagram.png)