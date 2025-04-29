package bptree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code InMemoryBPlusTree} class implements B+-trees.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class InMemoryBPlusTree<K extends Comparable<K>, P> extends BPlusTree<K, P> {

	/**
	 * The root {@code Node} of this {@code InMemoryBPlusTree}.
	 */
	Node<K> root;

	/**
	 * Constructs a {@code InMemoryBPlusTree}.
	 * 
	 * @param degree
	 *            the maximum number of pointers that each {@code Node} of this {@code InMemoryBPlusTree} can have
	 */
	public InMemoryBPlusTree(int degree) {
		super(degree);
	}

	/**
	 * Returns the root {@code Node} of this {@code InMemoryBPlusTree}.
	 * 
	 * @return the root {@code Node} of this {@code InMemoryBPlusTree}; {@code null} if this {@code InMemoryBPlusTree}
	 *         is empty
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public Node<K> root() throws IOException {
		return root;
	}

	/**
	 * Returns the specified child {@code Node} of the specified {@code NonLeafNode}.
	 * 
	 * @param node
	 *            a {@code NonLeafNode}
	 * @param i
	 *            the index of the child {@code Node}
	 * @return the specified child {@code Node} of the specified {@code NonLeafNode}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public Node<K> child(NonLeafNode<K> node, int i) throws IOException {
		return node.pointer(i);
	}

	/**
	 * Inserts the specified key and pointer into this {@code InMemoryBPlusTree}.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 * @throws InvalidInsertionException
	 *             if a key already existent in this {@code InMemoryBPlusTree} is attempted to be inserted again in the
	 *             {@code InMemoryBPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public void insert(K k, P p) throws InvalidInsertionException, IOException {
		if (root == null) {// if the tree is empty
			LeafNode<K, P> l = new LeafNode<K, P>(degree); // create an empty root node
			l.insert(k, p); // insert the specified key and pointer into leaf node l
			setRoot(l); // register node l as the new root
		} else { // if the tree is not empty
			HashMap<Node<K>, NonLeafNode<K>> node2parent = new HashMap<Node<K>, NonLeafNode<K>>();
			// to remember the parent of each visited node
			LeafNode<K, P> l = find(k, root, node2parent); // find leaf node l that should contain the specified key
			if (l.contains(k)) // no duplicate keys are allowed in the tree
				throw new InvalidInsertionException("key: " + k);
			if (!l.isFull()) { // if leaf node l has room for the specified key
				l.insert(k, p); // insert the specified key and pointer into leaf node l
			} else { // if leaf node l is full and thus needs to be split
				LeafNode<K, P> t = new LeafNode<K, P>(degree + 1); // create a temporary leaf node t
				t.append(l, 0, degree - 2); // copy everything to temporary node t
				t.insert(k, p); // insert the key and pointer into temporary node t
				LeafNode<K, P> lp = new LeafNode<K, P>(degree); // create a new leaf node lp
				lp.setSuccessor(l.successor()); // chaining from lp to the next leaf node
				l.clear(); // clear leaf node l
				int m = (int) Math.ceil(degree / 2.0); // compute the split point
				l.append(t, 0, m - 1); // copy the first half to leaf node l
				lp.append(t, m, degree - 1); // copy the second half to leaf node lp
				l.setSuccessor(lp); // chaining from leaf node l to leaf node lp
				insertInParent(l, lp.key(0), lp, node2parent); // use lp's first key as the separating key
			}
		}
	}

	/**
	 * Finds the {@code LeafNode} that is a descendant of the specified {@code Node} and must be responsible for the
	 * specified key.
	 * 
	 * @param k
	 *            a search key
	 * @param n
	 *            a {@code Node}
	 * @param node2parent
	 *            a {@code Map} to remember, for each visited {@code Node}, the parent of that {@code Node}
	 * @return the {@code LeafNode} which is a descendant of the specified {@code Node} and must be responsible for the
	 *         specified key
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@SuppressWarnings("unchecked")
	protected LeafNode<K, P> find(K k, Node<K> n, Map<Node<K>, NonLeafNode<K>> node2parent)
			throws IOException {
		if (n instanceof LeafNode)
			return (LeafNode<K, P>) n;
		else {
			Node<K> c = ((NonLeafNode<K>) n).child(k);
			node2parent.put(c, (NonLeafNode<K>) n);
			return find(k, c, node2parent);
		}
	}

	/**
	 * Inserts the specified key into the parent {@code Node} of the specified {@code Nodes}.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @param k
	 *            the key between the {@code Node}s
	 * @param np
	 *            a {@code Node}
	 * @param node2parent
	 *            a {@code Map} remembering, for each visited {@code Node}, the parent of that {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void insertInParent(Node<K> n, K k, Node<K> np,
			Map<Node<K>, NonLeafNode<K>> node2parent) throws IOException {
		if (n.equals(root)) { // if n is the root of the tree
			NonLeafNode<K> r = new NonLeafNode<K>(degree, n, k, np);
			setRoot(r); // a new root node r containing n, k, np and register it as the new root
			return;
		}
		NonLeafNode<K> p = node2parent.get(n); // find the parent p of n
		if (!p.isFull()) { // if parent node p has room for a new entry
			p.insertAfter(k, np, n); // insert k and np right after n
		} else { // if p is full and thus needs to be split
			NonLeafNode<K> t = new NonLeafNode<K>(degree + 1); // crate a temporary node
			t.copy(p, 0, p.keyCount()); // copy everything of p to the temporary node
			t.insertAfter(k, np, n); // insert k and np after n
			p.clear(); // clear p
			NonLeafNode<K> pp = new NonLeafNode<K>(degree); // create a new node pp
			int m = (int) Math.ceil((degree + 1) / 2.0); // compute the split point
			p.copy(t, 0, m - 1); // copy the first half to parent node p
			pp.copy(t, m, degree); // copy the second half to new node pp
			insertInParent(p, t.key(m - 1), pp, node2parent); // use the middle key as the separating key
		}
	}

	/**
	 * Saves the specified {@code Node} as the new root {@code Node}.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void setRoot(Node<K> n) throws IOException {
		this.root = n;
	}

	/**
	 * Removes the specified key and the corresponding pointer from this {@code InMemoryBPlusTree}.
	 * 
	 * @param k
	 *            the key to delete
	 * @throws InvalidDeletionException
	 *             if a key non-existent in a {@code InMemoryBPlusTree} is attempted to be deleted from the
	 *             {@code InMemoryBPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public void delete(K k) throws InvalidDeletionException, IOException {
		// please implement the body of this method
		HashMap<Node<K>, NonLeafNode<K>> node2parent = new HashMap<Node<K>, NonLeafNode<K>>();
		Node<K> l = find(k, root, node2parent);
		// node2parent associates each node on the search path with the parent node of that node
		if (l != null)
			delete(l, k, node2parent);
	}

	/**
	 * Removes the specified key and the corresponding pointer from the specified {@code Node}.
	 * 
	 * @param n
	 *            the {@code Node} from which the key and pointer are removed
	 * @param k
	 *            the key to remove
	 * @param node2parent
	 *            a {@code Map} remembering, for each visited {@code Node}, the parent of that {@code Node}
	 * @throws InvalidDeletionException
	 *             if a key non-existent in a {@code InMemoryBPlusTree} is attempted to be deleted from the
	 *             {@code InMemoryBPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void delete(Node<K> n, K k, Map<Node<K>, NonLeafNode<K>> node2parent)
			throws InvalidDeletionException, IOException {

		n.remove(k); //remove K from n

		if(n == root) //if n is the root
		{
			if(n.GetChildrenCount() == 1) //if N only has one child
			{
				setRoot(n.pointer(0)); //set that to the new root
				return;
			}
		}
		else if(n.isUnderUtilized())
		{
			//N`: previous or next child of parent(N)
			//K`: the key between pointers N and N` in parent n
			var parent = node2parent.get(n);
			int index = parent.indexOf(n);

			Node<K> leftSibling = index > 0 ? parent.pointer(index - 1) : null; //check if we have a left sibling
			Node<K> rightSibling = index < parent.pointerCount() - 1 ? parent.pointer(index + 1) : null; //check if we have a right sibling

			// Try to merge with left if safe
			if (leftSibling != null && leftSibling.mergeable(n)) {
				K separatorKey = parent.key(index - 1);
				merge(leftSibling, separatorKey, n, node2parent);
			}
			else if (rightSibling != null && n.mergeable(rightSibling)) { //merge with right if safe
				K separatorKey = parent.key(index);
				merge(n, separatorKey, rightSibling, node2parent);
			}

			// Redistribute from left if merging is unsafe
			else if (leftSibling != null) {
				K separatorKey = parent.key(index - 1);
				if (n instanceof LeafNode) {
					redistributeRightLeaf((LeafNode<K, P>) leftSibling, separatorKey, (LeafNode<K, P>) n, parent);
				} else {
					redistributeRightNonLeaf((NonLeafNode<K>) leftSibling, separatorKey, (NonLeafNode<K>) n, parent);
				}
			}
			else if (rightSibling != null) { //Redistribute from right if merging is unsafe
				K separatorKey = parent.key(index);
				if (n instanceof LeafNode) {
					redistributeLeftLeaf((LeafNode<K, P>) n, separatorKey, (LeafNode<K, P>) rightSibling, parent);
				} else {
					redistributeLeftNonLeaf((NonLeafNode<K>) n, separatorKey, (NonLeafNode<K>) rightSibling, parent);
				}
			}
		}
		else
		{
			return; //not underutilized
		}
	}

	/**
	 * Merges the specified {@code Node}s.
	 * 
	 * @param np
	 *            a {@code Node}
	 * @param kp
	 *            a key
	 * @param n
	 *            a {@code Node}
	 * @param node2parent
	 *            a {@code Map} remembering, for each visited {@code Node}, the parent of that {@code Node}
	 * @throws InvalidDeletionException
	 *             if a key non-existent in a {@code InMemoryBPlusTree} is attempted to be deleted from the
	 *             {@code InMemoryBPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@SuppressWarnings("unchecked")
	protected void merge(Node<K> np, K kp, Node<K> n, Map<Node<K>, NonLeafNode<K>> node2parent)
			throws InvalidDeletionException, IOException {
		if (n instanceof NonLeafNode) // nonleaf node
			merge((NonLeafNode<K>) np, kp, (NonLeafNode<K>) n);
		else // nonleaf node
			merge((LeafNode<K, P>) np, (LeafNode<K, P>) n);
		NonLeafNode<K> parent = node2parent.get(n);
		if (parent == null)
			parent = node2parent.get(np);
		delete(parent, kp, node2parent);
	}

	/**
	 * Merges the specified {@code NonLeafNode}s.
	 * 
	 * @param left
	 *            a {@code NonLeafNode}
	 * @param sepKey
	 *            a key
	 * @param right
	 *            a {@code NonLeafNode}
	 */
	@SuppressWarnings("unchecked")
	protected void merge(NonLeafNode<K> left, K sepKey, NonLeafNode<K> right) {
		// store a copy of the current key count
		int oldKC = left.keyCount;
		// put the seperator key in the last index of the left node
		left.keys[oldKC] = sepKey;
		// put the pointer to the right node in the next index of the left node
		left.pointers[oldKC + 1] = right.pointer(0);
		left.keyCount++; //increment key count

		// now append every key, pointer from the right node to the left node
		for (int i = 0; i < right.keyCount; i++) {
			left.keys[left.keyCount] = right.key(i);
			left.pointers[left.keyCount + 1] = right.pointer(i + 1);
			left.keyCount++;
		}
	}

	/**
	 * Merges the specified {@code LeafNode}s.
	 * 
	 * @param np
	 *            a {@code LeafNode}
	 * @param n
	 *            a {@code LeafNode}
	 */
	protected void merge(LeafNode<K, P> np, LeafNode<K, P> n) {
		// append all key/pointer pairs
		np.append(n, 0, n.keyCount - 1);
		// assign last pointer
		np.pointers[np.pointers.length - 1] = n.pointers[n.pointers.length - 1];
	}

	/**
	 * Moves a key and a pointer from the first specified {@code NonLeafNode} to the second specified
	 * {@code NonLeafNode}.
	 * 
	 * @param np
	 *            a {@code NonLeafNode} from which a key and a pointer are removed
	 * @param kp
	 *            the key between the specified {@code NonLeafNode}s
	 * @param n
	 *            a {@code NonLeafNode} to which a key and a pointer are added
	 * @param parent
	 *            a {@code NonLeafNode} that is the parent of the specified {@code NonLeafNode}s.
	 */
	protected void redistributeRightNonLeaf(NonLeafNode<K> np, K kp, NonLeafNode<K> n,
			NonLeafNode<K> parent) {
		int m = np.keyCount(); // let m be the index of the last pointer in np
		K k = np.key(m - 1); // let k be the last key from np
		n.insert(kp, 0, np.pointer(m), 0);
		// insert the last key and pointer from np at the beginning of n
		np.delete(m - 1, m); // remove the last key and pointer from np
		parent.changeKey(np, n, k); // let k be the new key between np and p in their parent node
	}

	/**
	 * Moves a key and a pointer from the second specified {@code NonLeafNode} to the first specified
	 * {@code NonLeafNode}.
	 * 
	 * @param n
	 *            a {@code NonLeafNode} to which a key and a pointer are added
	 * @param kp
	 *            the key between the specified {@code NonLeafNode}s
	 * @param np
	 *            a {@code NonLeafNode} from which a key and a pointer are removed
	 * @param parent
	 *            a {@code NonLeafNode} that is the parent of the specified {@code NonLeafNode}s.
	 */
	protected void redistributeLeftNonLeaf(NonLeafNode<K> n, K kp, NonLeafNode<K> np,
			NonLeafNode<K> parent) {

		K movedUp = np.key(0); //get nps first key
		Node<K> movedPtr = np.pointer(0);
		//insert the old separator + movedPtr at the end of n
		n.insert(kp, n.keyCount,movedPtr, n.keyCount + 1);
		// remove np’s first key and its first pointer
		np.delete(0, 0);
		// 4) update the parents separating key
		parent.changeKey(n, np, movedUp);
	}

	/**
	 * Moves a key and a pointer from the first specified {@code LeafNode} to the second specified {@code LeafNode}.
	 * 
	 * @param np
	 *            a {@code LeafNode} from which a key and a pointer are removed
	 * @param kp
	 *            the key between the specified {@code LeafNode}s
	 * @param n
	 *            a {@code LeafNode} to which a key and a pointer are added
	 * @param parent
	 *            a {@code NonLeafNode} that is the parent of the specified {@code LeafNode}s.
	 */
	protected void redistributeRightLeaf(LeafNode<K, P> np, K kp, LeafNode<K, P> n, NonLeafNode<K> parent) {
		int m = np.keyCount() - 1; // let me be the index of the last key and pointer in npS
		K k = np.key(m); // let k be the last key from np
		n.insert(0, k, np.pointer(m)); // insert the last key and pointer from np at the beginning of n
		np.delete(m); // remove the last key and pointer from np
		parent.changeKey(np, n, k); // let k be the new key between np and p in their parent node
	}

	/**
	 * Moves a key and a pointer from the second specified {@code LeafNode} to the first specified {@code LeafNode}.
	 * 
	 * @param n
	 *            a {@code LeafNode} to which a key and a pointer are added
	 * @param kp
	 *            the key between the specified {@code LeafNode}s
	 * @param np
	 *            a {@code LeafNode} from which a key and a pointer are removed
	 * @param parent
	 *            a {@code NonLeafNode} that is the parent of the specified {@code LeafNode}s.
	 */
	protected void redistributeLeftLeaf(LeafNode<K, P> n, K kp, LeafNode<K, P> np, NonLeafNode<K> parent) {
		// get the first entry from the right sibling
		K movedKey = np.key(0);
		P movedPtr = np.pointer(0);
		//append it to the other node
		n.insert(n.keyCount, movedKey, movedPtr);
		//remove it from the right sibling
		np.delete(0);
		//update the parent separator to the new first key of the right sibling
		K newSeparator = np.key(0);
		parent.changeKey(n, np, newSeparator);
	}

}
