package eu.funinnumbers.guardian.util;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 * <p/>
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 * <p/>
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * <p/>
 * <p>As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs.  Higher values decrease the space overhead
 * but increase the lookup cost (reflected in most of the operations of the
 * <tt>HashMap</tt> class, including <tt>get</tt> and <tt>put</tt>).  The
 * expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the
 * number of rehash operations.  If the initial capacity is greater
 * than the maximum number of entries divided by the load factor, no
 * rehash operations will ever occur.
 * <p/>
 * <p>If many mappings are to be stored in a <tt>HashMap</tt> instance,
 * creating it with a sufficiently large capacity will allow the mappings to
 * be stored more efficiently than letting it perform automatic rehashing as
 * needed to grow the table.
 * <p/>
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 * <p/>
 * If no such object exists, the map should be "wrapped" using the
 * {@link java.util.Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 * <p/>
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link java.util.ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 * <p/>
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * <p/>
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author Doug Lea
 * @author Josh Bloch
 * @author Arthur van Hoff
 * @author Neal Gafter
 * @version 1.72, 04/24/06
 * @since 1.2
 */
public class HashMap { //NOPMD

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    transient volatile int modCount;

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *                                  or the load factor is nonpositive
     */
    public HashMap(final int initialCapacity, final float loadFactor) { //NOPMD
        int localCapacity = initialCapacity;
        if (localCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: "
                    + localCapacity);
        }
        if (localCapacity > MAXIMUM_CAPACITY) {
            localCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: "
                    + loadFactor);
        }

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < localCapacity) {
            capacity <<= 1;
        }

        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        table = new Entry[capacity];
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(final int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
    }

    // internal utilities

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits. Note: Null keys always map to hash 0, thus index 0.
     *
     * @param code the hash code
     * @return the new hash code
     */
    static int hash(final int code) {
        int myCode = code;
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        myCode ^= (myCode >>> 20) ^ (myCode >>> 12);
        return myCode ^ (myCode >>> 7) ^ (myCode >>> 4);
    }

    /**
     * Returns index for hash code h.
     *
     * @param code   the hash code
     * @param length the length of the code
     * @return the index of the hash code
     */
    static int indexFor(final int code, final int length) {
        return code & (length - 1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p/>
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * <p/>
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    public Object get(final Object key) { //NOPMD
        if (key == null) {
            return getForNullKey();
        }
        final int hash = hash(key.hashCode());
        for (Entry entry = table[indexFor(hash, table.length)];
             entry != null;
             entry = entry.next) {
            Object entryKey;
            if (entry.hash == hash && ((entryKey = entry.key) == key || key.equals(entryKey))) {
                return entry.value;
            }
        }
        return null;
    }

    /**
     * Offloaded version of get() to look up null keys.  Null keys map
     * to index 0.  This null case is split out into separate methods
     * for the sake of performance in the two most commonly used
     * operations (get and put), but incorporated with conditionals in
     * others.
     */
    private Object getForNullKey() {
        for (Entry e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     */
    public boolean containsKey(final Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns the entry associated with the specified key in the
     * HashMap.  Returns null if the HashMap contains no mapping
     * for the key.
     */
    final Entry getEntry(final Object key) { //NOPMD
        final int hash = (key == null) ? 0 : hash(key.hashCode());
        for (Entry entry = table[indexFor(hash, table.length)];
             entry != null;
             entry = entry.next) {
            Object entryKey;
            if (entry.hash == hash
                    && ((entryKey = entry.key) == key || (key != null && key.equals(entryKey)))) {
                return entry;
            }
        }
        return null;
    }


    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public Object put(final Object key, final Object value) { //NOPMD
        if (key == null) {
            return putForNullKey(value);
        }
        final int hash = hash(key.hashCode());
        final int index = indexFor(hash, table.length);
        for (Entry entry = table[index]; entry != null; entry = entry.next) {
            Object entryKey;
            if (entry.hash == hash && ((entryKey = entry.key) == key || key.equals(entryKey))) {
                final Object oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, index);
        return null;
    }

    /**
     * Offloaded version of put for null keys.
     */
    private Object putForNullKey(final Object value) {
        for (Entry e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                final Object oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        modCount++;
        addEntry(0, null, value, 0);
        return null;
    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     * <p/>
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two;
     *                    must be greater than current capacity unless current
     *                    capacity is MAXIMUM_CAPACITY (in which case value
     *                    is irrelevant).
     */
    void resize(final int newCapacity) {
        final Entry[] oldTable = table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        final Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfers all entries from current table to newTable.
     */
    void transfer(final Entry[] newTable) {
        Entry[] src = table;
        final int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry entry = src[j];
            if (entry != null) {
                src[j] = null;
                do {
                    final Entry next = entry.next;
                    final int index = indexFor(entry.hash, newCapacity);
                    entry.next = newTable[index];
                    newTable[index] = entry;
                    entry = next;
                } while (entry != null);
            }
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public Object remove(final Object key) {
        final Entry entry = removeEntryForKey(key);
        return (entry == null ? null : entry.value);
    }

    /**
     * Removes and returns the entry associated with the specified key
     * in the HashMap.  Returns null if the HashMap contains no mapping
     * for this key.
     */
    final public Entry removeEntryForKey(final Object key) { //NOPMD
        final int hash = (key == null) ? 0 : hash(key.hashCode());
        final int index = indexFor(hash, table.length);
        Entry prev = table[index];
        Entry entry = prev;

        while (entry != null) {
            final Entry next = entry.next;
            Object entryKey;
            if (entry.hash == hash
                    && ((entryKey = entry.key) == key || (key != null && key.equals(entryKey)))) {
                modCount++;
                size--;
                if (prev == entry) { //NOPMD
                    table[index] = next;
                } else {
                    prev.next = next;
                }
                return entry;
            }
            prev = entry;
            entry = next;
        }

        return entry;
    }

    /**
     * Special version of remove for EntrySet.
     */
    final Entry removeMapping(final Object obj) { //NOPMD
        if (!(obj instanceof Map.Entry)) {
            return null;
        }

        final Map.Entry entry = (Map.Entry) obj;
        final Object key = entry.getKey();
        final int hash = (key == null) ? 0 : hash(key.hashCode());
        final int index = indexFor(hash, table.length);
        Entry prev = table[index];
        Entry thisEntry = prev;

        while (thisEntry != null) {
            final Entry next = thisEntry.next;
            if (thisEntry.hash == hash && thisEntry.equals(entry)) {
                modCount++;
                size--;
                if (prev == thisEntry) { //NOPMD
                    table[index] = next;
                } else {
                    prev.next = next;
                }
                return thisEntry;
            }
            prev = thisEntry;
            thisEntry = next;
        }

        return thisEntry;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        modCount++;
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            tab[i] = null;
        }
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(final Object value) { //NOPMD
        if (value == null) {
            return containsNullValue();
        }

        final Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Special-case code for containsValue with null argument.
     */
    private boolean containsNullValue() {
        final Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (e.value == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Entry implements Map.Entry { //NOPMD

        final Object key;
        public Object value;
        public Entry next;
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(final int hash, final Object key, final Object value, final Entry next) {
            this.value = value;
            this.next = next;
            this.key = key;
            this.hash = hash;
        }

        public final Object getKey() {
            return key;
        }

        public final Object getValue() {
            return value;
        }

        public final Object setValue(final Object newValue) {
            final Object oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(final Object obj) { //NOPMD
            if (!(obj instanceof Map.Entry)) {
                return false;
            }

            final Map.Entry thatEntry = (Map.Entry) obj;
            final Object thisKey = getKey();
            final Object thatKey = thatEntry.getKey();
            if (thisKey == thatKey || (thisKey != null && thisKey.equals(thatKey))) { //NOPMD
                final Object thisValue = getValue();
                final Object thatValue = thatEntry.getValue();
                if (thisValue == thatValue || (thisValue != null && thisValue.equals(thatValue))) { //NOPMD
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }
    }

    /**
     * Adds a new entry with the specified key, value and hash code to
     * the specified bucket.  It is the responsibility of this
     * method to resize the table if appropriate.
     * <p/>
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(final int hash, final Object key, final Object value, final int bucketIndex) {
        final Entry entry = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, entry);
        if (size++ >= threshold) {
            resize(2 * table.length);
        }
    }

    /**
     * Like addEntry except that this version is used when creating entries
     * as part of Map construction or "pseudo-construction" (cloning,
     * deserialization).  This version needn't worry about resizing the table.
     * <p/>
     * Subclass overrides this to alter the behavior of HashMap(Map),
     * clone, and readObject.
     */
    void createEntry(final int hash, final Object key, final Object value, final int bucketIndex) {
        final Entry entry = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, entry);
        size++;
    }

    // These methods are used when serializing HashSets
    int capacity() {
        return table.length;
    }

    float loadFactor() {
        return loadFactor;
    }

    /**
     * @return an enumerator with the entries of a hashmap
     */
    public Enumeration getEntries() {
        final Vector entries = new Vector(); //NOPMD
        final int totEntries = table.length;
        int index = 0;
        for (index = 0; index < totEntries; index++) {
            Entry entry = table[index];
            while (entry != null) {
                entries.addElement(entry);
                entry = entry.next;
            }
        }

        return entries.elements();
    }

    /**
     * @return a vector with the values of a HashMap
     */
    public Vector getValues() {
        final Vector entries = new Vector(); //NOPMD
        final int totEntries = table.length;
        int index = 0;
        for (index = 0; index < totEntries; index++) {
            Entry entry = table[index];
            while (entry != null) {
                entries.addElement(entry.value);
                entry = entry.next;
            }
        }

        return entries;
    }

    /**
     * @return an enumerator with the keys of a HashMap
     */    
    public Enumeration getKeys() {
        final Vector entries = new Vector(); //NOPMD
        final int totEntries = table.length;
        int index = 0;
        for (index = 0; index < totEntries; index++) {
            Entry entry = table[index];
            while (entry != null) {
                entries.addElement(entry.key);
                entry = entry.next;
            }
        }

        return entries.elements();
    }

    /**
     * @return the HashMap table
     */
    public Entry[] getTable() {
        return table;
    }
}
