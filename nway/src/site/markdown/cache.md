Cache explained
===============

For a quick introduction I suggest this [document](http://csillustrated.berkeley.edu/PDFs/handouts/cache-3-associativity-handout.pdf)
 
## Cache Mapping and Associativity (source: [pc guide](http://www.pcguide.com/ref/mbsys/cache/funcMapping-c.html))
 
A very important factor in determining the effectiveness of the cache relates to how the cache is mapped
to the system memory. What this means in brief is that there are many different ways to allocate the storage in 
our cache to the memory addresses it serves. Let's take as an org.mirko.cache.example a system with 512 KB of L2 cache and 
64 MB of main memory. The burning question is: how do we decide how to divvy up the 16,384 address lines in 
our cache amongst the "huge" 64 MB of memory?
 
There are three different ways that this mapping can generally be done. The choice of mapping technique is 
so critical to the design that the cache is often named after this choice:
 
* **Direct Mapped Cache**: The simplest way to allocate the cache to the system memory is to determine 
how many cache lines there are (16,384 in our org.mirko.cache.example) and just chop the system memory into the same number 
of chunks. Then each chunk gets the use of one cache line. This is called direct mapping. So if we have 
64 MB of main memory addresses, each cache line would be shared by 4,096 memory addresses (64 M divided by 
16 K).
* **Fully Associative Cache**: Instead of hard-allocating cache lines to particular memory locations, it is 
possible to design the cache so that any line can store the contents of any memory location. This is called 
fully associative mapping.
* **N-Way Set Associative Cache**: "N" here is a number, typically 2, 4, 8 etc. This is a compromise between 
the direct mapped and fully associative designs. In this case the cache is broken into sets where each set 
contains "N" cache lines, let's say 4. Then, each memory address is assigned a set, and can be cached in any 
one of those 4 locations within the set that it is assigned to. In other words, within each set the cache is 
associative, and thus the name.  
This design means that there are "N" possible places that a given memory location may be in the cache. The 
tradeoff is that there are "N" times as many memory locations competing for the same "N" lines in the set. 
Let's suppose in our org.mirko.cache.example that we are using a 4-way set associative cache. So instead of a single block of 
16,384 lines, we have 4,096 sets with 4 lines in each. Each of these sets is shared by 16,384 memory addresses 
(64 M divided by 4 K) instead of 4,096 addresses as in the case of the direct mapped cache. So there is more to 
share (4 lines instead of 1) but more addresses sharing it (16,384 instead of 4,096).


Conceptually, the direct mapped and fully associative caches are just "special cases" of the N-way set 
associative cache. You can set "N" to 1 to make a "1-way" set associative cache. If you do this, then there 
is only one line per set, which is the same as a direct mapped cache because each memory address is back to 
pointing to only one possible cache location. On the other hand, suppose you make "N" really large; say, you 
set "N" to be equal to the number of lines in the cache (16,384 in our org.mirko.cache.example). If you do this, then you only 
have one set, containing all of the cache lines, and every memory location points to that huge set. This means 
that any memory address can be in any line, and you are back to a fully associative cache.

## Cache Algorithms (source: wikipedia)

In computing, cache algorithms (also frequently called replacement algorithms or replacement policies) 
are optimizing instructions – or algorithms – that a computer program or a hardware-maintained structure 
can follow, in order to manage a cache of information stored on the computer. When the cache is full, the 
algorithm must choose which items to discard to make room for the new ones.

### Least Recently Used (LRU)

Discards the least recently used items first. This algorithm requires keeping track of what was used when, 
which is expensive if one wants to make sure the algorithm always discards ''the'' least recently used item. 
General implementations of this technique require keeping ''age bits'' for cache-lines and track the 
''Least Recently Used'' cache-line based on age-bits. In such an implementation, every time a cache-line is used,
the age of all other cache-lines changes. LRU is actually a [family of caching algorithms]
(http://en.wikipedia.org/wiki/Page_replacement_algorithm#Variants_on_LRU) with members including,
[2Q by Theodore Johnson and Dennis Shasha](http://www.vldb.org/conf/1994/P439.PDF) 
and LRU/K by Pat O'Neil, Betty O'Neil and Gerhard Weikum.

### Most Recently Used (MRU)

Discards, in contrast to LRU, the most recently used items first. In findings presented at the 11th VLDB 
conference, Chou and Dewitt noted that ''When a file is being repeatedly scanned in a *Looping Sequential* 
reference pattern, MRU is the best [replacement algorithm](http://en.wikipedia.org/wiki/Page_replacement_algorithm)
.'' Subsequently other researchers presenting at the 22nd VLDB conference noted that for random access 
patterns and repeated scans over large datasets (sometimes known as cyclic access patterns) MRU cache 
algorithms have more hits than LRU due to their tendency to retain older data. MRU algorithms are most
 useful in situations where the older an item is, the more likely it is to be accessed.
