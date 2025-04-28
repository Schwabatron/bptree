package bptree;

import bptree.BPlusTree.InvalidDeletionException;

/**
 * The {@code LeafNode} class implements leaf nodes in a B+-tree. {@code LeafNode}s are chained so each {@code LeafNode}
 * except the last {@code LeafNode} has a successor.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class LeafNode<K extends Comparable<K>, P> extends Node<K> {

	/**
	 * An automatically generated serial version UID.
	 */
	private static final long serialVersionUID = 2590729339527002169L;

	/**
	 * Constructs a {@code LeafNode}.
	 * 
	 * @param degree
	 *            the degree of the {@code LeafNode}
	 */
	public LeafNode(int degree) {
		super(degree);
	}

	/**
	 * Inserts the specified key and pointer assuming that this {@code LeafNode} has room for them.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 */
	public void insert(K k, P p) {
		if (keyCount == 0 || k.compareTo(keys[0]) < 0)
			insert(0, k, p);
		else {
			int i = findIndexL(k);
			insert(i + 1, k, p);
		}
	}

	/**
	 * Inserts the specified key and pointer at the specified index.
	 * 
	 * @param i
	 *            the index at which the key and pointer are inserted
	 * @param k
	 *            a key
	 * @param p
	 *            a pointer
	 */
	public void insert(int i, K k, P p) {
		for (int j = keyCount; j > i; j--) {
			keys[j] = keys[j - 1];
			pointers[j] = pointers[j - 1];
		}
		keys[i] = k;
		pointers[i] = p;
		keyCount++;
	}

	/**
	 * Removes a key and a pointer at the specified index.
	 * 
	 * @param i
	 *            the index at which the key and pointer are deleted
	 */
	public void delete(int i) {
		for (int j = i; j < keyCount - 1; j++) {
			keys[j] = keys[j + 1];
			pointers[j] = pointers[j + 1];
		}
		keys[keyCount - 1] = null;
		pointers[keyCount - 1] = null;
		keyCount--;
	}

	/**
	 * Returns the largest index i such that keys[i] < the given key.
	 * 
	 * @param key
	 *            a key
	 * @return the largest index i such that keys[i] < the given key; -1 if there is no such i
	 */
	protected int findIndexL(K key) {
		for (int i = keyCount - 1; i >= 0; i--) {
			if (keys[i].compareTo(key) < 0)
				return i;
		}
		return -1;
	}

	/**
	 * Sets the pointer to the successor of this {@code LeafNode}.
	 * 
	 * @param p
	 *            a pointer to the new successor of this {@code LeafNode}
	 * @return a pointer to the previous successor of this {@code LeafNode}
	 */
	public <N> N setSuccessor(N p) {
		@SuppressWarnings("unchecked")
		N s = (N) pointers[pointers.length - 1];
		pointers[pointers.length - 1] = p;
		return s;
	}

	/**
	 * Determines whether or not the specified key is contained in this {@code LeafNode}.
	 * 
	 * @param k
	 *            a key
	 * @return {@code true} if the specified key is contained in this {@code LeafNode}; {@code false} otherwise
	 */
	public boolean contains(K k) {
		for (int i = 0; i < keyCount; i++)
			if (keys[i].compareTo(k) == 0)
				return true;
		return false;
	}

	/**
	 * Removes the specified key and a relevant pointer from this {@code LeafNode}.
	 * 
	 * @param key
	 *            a key
	 * @throws InvalidDeletionException
	 *             if a key non-existent in this {@code LeafNode} is attempted to be removed from this {@code LeafNode}.
	 */
	@Override
	public void remove(K key) throws InvalidDeletionException {
		int i = 0;
		for (; i < keyCount; i++) {
			if (keys[i].compareTo(key) == 0) {
				for (int j = i; j < keyCount - 1; j++) {
					keys[j] = keys[j + 1];
					pointers[j] = pointers[j + 1];
				}
				break;
			}
		}
		if (i == keyCount)
			throw new InvalidDeletionException("key: " + key);
		keyCount--;
		keys[keyCount] = null;
		pointers[keyCount] = null;
	}

	/**
	 * Determines whether or not this {@code LeafNode} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code LeafNode} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	@Override
	public boolean isUnderUtilized() {
		if(keyCount < Math.ceil((degree - 1.0) / 2.0)) //checking if this leafnode has the min amount of keys
		{
			return true; //if the leaf node has too little keys then it is under utilized
		}
		else
		{
			return false; //if the leaf node has enough keys or more then it is utilized
		}
	}

	/**
	 * Determines whether or not this {@code LeafNode} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code LeafNode} can be merged with the specified {@code Node}; {@code false}
	 *         otherwise
	 */
	@Override
	public boolean mergeable(Node<K> other) {
		int combined_keyCount = keyCount + other.keyCount;
		if(combined_keyCount <= (degree - 1)) //degree - 1 = max number of key entries
		{
			return true; // if the combined keycount is less than or equal to the max number of keys then it is not able to merge
		}
		else {
			return false; // if the combined keycount goes over the max number of keys then it is not able to merge
		}
	}

	/**
	 * Returns a pointer to the succeeding {@code LeafNode}.
	 * 
	 * @return a pointer to the succeeding {@code LeafNode}; {@code null} if no succeeding {@code LeafNode}
	 */
	@SuppressWarnings("unchecked")
	public <N> N successor() {
		return (N) pointers[pointers.length - 1];
	}

}
