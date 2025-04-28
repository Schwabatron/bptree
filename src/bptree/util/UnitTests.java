package bptree.util;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import bptree.BPlusTree;
import bptree.InMemoryBPlusTree;
import bptree.Node;
import bptree.NonLeafNode;

/**
 * {@code UnitTests} tests the implementations in the {@code bptree} package.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTests {

	static InMemoryBPlusTree<String, Integer> tree = new InMemoryBPlusTree<String, Integer>(
			3);

	static HashMap<Object, Integer> m = new HashMap<Object, Integer>();

	/**
	 * Tests the Task 1 implementation.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void test1() throws Exception {
		tree.insert("c", 1);
		tree.insert("d", 2);
		tree.insert("f", 3);
		tree.insert("a", 4);
		tree.insert("b", 5);
		tree.insert("g", 6);
		tree.delete("a");
		tree.delete("g");
		assertEquals(" @0(@1, d, @2, null, null)\n" + "  @1(@3, c, @4, null, null)\n"
				+ "   @3(5, b, null, null, @4)\n" + "   @4(1, c, null, null, @5)\n"
				+ "  @2(@5, f, @6, null, null)\n" + "   @5(2, d, null, null, @6)\n"
				+ "   @6(3, f, null, null, null)",
				toString(tree.root(), 1, tree.degree(), m));
	}

	/**
	 * Tests the Task 2 implementation.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void test2() throws Exception {
		tree.insert("g", 6);
		tree.insert("h", 7);
		tree.delete("d");
		assertEquals(" @0(@1, d, @2, null, null)\n" + "  @1(@3, c, @4, null, null)\n"
				+ "   @3(5, b, null, null, @4)\n" + "   @4(1, c, null, null, @5)\n"
				+ "  @2(@5, h, @7, null, null)\n" + "   @5(3, f, 6, g, @7)\n"
				+ "   @7(7, h, null, null, null)",
				toString(tree.root(), 1, tree.degree(), m));
		tree.delete("h");
		assertEquals(
				" @1(@3, c, @4, d, @5)\n" + "  @3(5, b, null, null, @4)\n"
						+ "  @4(1, c, null, null, @5)\n" + "  @5(3, f, 6, g, null)",
				toString(tree.root(), 1, tree.degree(), m));
	}

	/**
	 * Tests the Task 3 implementation.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void test3() throws Exception {
		tree.insert("a", 2);
		tree.insert("d", 4);
		tree.insert("e", 6);
		tree.insert("h", 7);
		System.out.println(toString(tree.root(), 1, tree.degree(), m));
		System.out.println("before delete c:\n"
				+ toString(tree.root(), 1, tree.degree(), m));
		tree.delete("c");
		// you can also print right after delete:
		System.out.println(" after delete c:\n"
				+ toString(tree.root(), 1, tree.degree(), m));
		assertEquals(
				" @8(@1, f, @9, null, null)\n" + "  @1(@3, d, @5, null, null)\n"
						+ "   @3(2, a, 5, b, @5)\n" + "   @5(4, d, 6, e, @10)\n"
						+ "  @9(@10, g, @11, null, null)\n"
						+ "   @10(3, f, null, null, @11)\n" + "   @11(6, g, 7, h, null)",
				toString(tree.root(), 1, tree.degree(), m));
	}

	/**
	 * Tests the Task 4 implementation.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void test4() throws Exception {
		InMemoryBPlusTree<String, Integer> tree2 = new InMemoryBPlusTree<String, Integer>(
				3);
		HashMap<Object, Integer> m2 = new HashMap<Object, Integer>();

		tree2.insert("c", 1);
		tree2.insert("d", 2);
		tree2.insert("f", 3);
		tree2.delete("d");
		assertEquals(
				" @0(@1, f, @2, null, null)\n" + "  @1(1, c, null, null, @2)\n"
						+ "  @2(3, f, null, null, null)",
				toString(tree2.root(), 1, tree2.degree(), m2));
		assertThrows(BPlusTree.InvalidDeletionException.class, () -> tree2.delete("e"));
		tree2.delete("c");
		assertEquals(" @1(3, f, null, null, null)",
				toString(tree2.root(), 1, tree2.degree(), m2));

		InMemoryBPlusTree<String, Integer> tree3 = new InMemoryBPlusTree<String, Integer>(
				3);
		HashMap<Object, Integer> m3 = new HashMap<Object, Integer>();

		tree3.insert("a", 1);
		tree3.insert("b", 2);
		tree3.insert("c", 3);
		tree3.insert("d", 4);
		tree3.insert("e", 5);
		tree3.insert("f", 6);
		tree3.insert("g", 7);
		tree3.insert("h", 8);
		tree3.insert("i", 9);
		tree3.delete("a");
		assertEquals(
				" @0(@1, e, @2, null, null)\n" + "  @1(@3, c, @4, null, null)\n"
						+ "   @3(2, b, null, null, @4)\n" + "   @4(3, c, 4, d, @5)\n"
						+ "  @2(@5, g, @6, i, @7)\n" + "   @5(5, e, 6, f, @6)\n"
						+ "   @6(7, g, 8, h, @7)\n" + "   @7(9, i, null, null, null)",
				toString(tree3.root(), 1, tree3.degree(), m3));
		tree3.delete("b");
		assertEquals(
				" @0(@1, g, @2, null, null)\n" + "  @1(@3, e, @5, null, null)\n"
						+ "   @3(3, c, 4, d, @5)\n" + "   @5(5, e, 6, f, @6)\n"
						+ "  @2(@6, i, @7, null, null)\n" + "   @6(7, g, 8, h, @7)\n"
						+ "   @7(9, i, null, null, null)",
				toString(tree3.root(), 1, tree3.degree(), m3));

		InMemoryBPlusTree<String, Integer> tree4 = new InMemoryBPlusTree<String, Integer>(
				4);
		HashMap<Object, Integer> m4 = new HashMap<Object, Integer>();

		tree4.insert("c", 1);
		tree4.insert("d", 2);
		tree4.insert("f", 3);
		tree4.delete("d");
		assertEquals(" @0(1, c, 3, f, null, null, null)",
				toString(tree4.root(), 1, tree4.degree(), m4));
		tree4.delete("f");
		assertEquals(" @0(1, c, null, null, null, null, null)",
				toString(tree4.root(), 1, tree4.degree(), m4));

		tree4.insert("a", 5);
		tree4.insert("b", 6);
		tree4.insert("i", 7);
		tree4.delete("b");
		assertEquals(" @0(5, a, 1, c, 7, i, null)",
				toString(tree4.root(), 1, tree4.degree(), m4));

		tree4.insert("g", 8);
		tree4.insert("h", 9);
		tree4.delete("c");
		assertEquals(
				" @1(@0, h, @2, null, null, null, null)\n"
						+ "  @0(5, a, 8, g, null, null, @2)\n"
						+ "  @2(9, h, 7, i, null, null, null)",
				toString(tree4.root(), 1, tree4.degree(), m4));
	}

	protected String toString(Node<String> node, int level, int degree,
			HashMap<Object, Integer> m) throws IOException {
		String s = String.format("%" + level + "s", "") + node.toString(m);
		if (node instanceof NonLeafNode) {
			for (int i = 0; i < degree; i++) {
				Node<String> child = tree.child((NonLeafNode<String>) node, i);
				if (child != null)
					s += "\n" + toString(child, level + 1, degree, m);
			}
		}
		return s;
	}

}
