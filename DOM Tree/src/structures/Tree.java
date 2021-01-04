package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		if(!sc.hasNext()) return;
		root = new TagNode(bracketRemoval(sc.nextLine()),null,null);
		Stack<TagNode> tagStack = new Stack<TagNode>();
		tagStack.push(root);
		TagNode NewNode;
		while(sc.hasNext()) {
			 String s = sc.nextLine();
			 if(s.charAt(0)=='<'&&s.charAt(1)!='/') {
				 NewNode = new TagNode(bracketRemoval(s),null,null);
				 if(tagStack.isEmpty()) {			
					 root.firstChild=NewNode;
					 tagStack.push(NewNode);
				 }
				 else {
					 if(tagStack.peek().firstChild!=null) {
						 TagNode ptr = tagStack.peek().firstChild;
						 while(ptr.sibling!=null) {
							 ptr=ptr.sibling;
						 }
						 ptr.sibling=NewNode;
						 tagStack.push(ptr.sibling);
					 }
					 else if(tagStack.peek().firstChild==null) {
						 tagStack.peek().firstChild=NewNode;
						 tagStack.push(tagStack.peek().firstChild);
					 }
				 }
			 }
		 else if(s.charAt(0)=='<'&&s.charAt(1)=='/') {
		       tagStack.pop();
			
		 }else {
			 if(tagStack.peek().firstChild!=null) {
				 TagNode ptr = tagStack.peek().firstChild;
					while(ptr.sibling!=null) {
						ptr=ptr.sibling;
					}
					ptr.sibling= new TagNode(s,null,null);
				}
				else if(tagStack.peek().firstChild==null) {
					tagStack.peek().firstChild=new TagNode(s,null,null);
				}
		  }
		}
		
}
	 private String bracketRemoval(String str) {
	     return str.substring(1,str.length()-1);
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(root==null)return;
		else replaceTag(this.root,oldTag,newTag);
	}
	private void replaceTag(TagNode n, String oldTag,String newTag) {
		if(n==null) return;
		if(n.tag.equals(oldTag))n.tag=newTag;	
		replaceTag(n.firstChild,oldTag,newTag);
		replaceTag(n.sibling,oldTag,newTag);
		
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		addBoldTag(root,row);
	}
	private void addBoldTag(TagNode n, int r) {
		if(n==null)return;
		if(n.tag.equals("table")) {
			int counter=0;
			TagNode curr = n.firstChild;
			boolean k=false;
			while(curr!=null) {
				if(curr.tag.equals("tr"))counter++;
				if(counter==r) {
					k=true;
					break;
				}
				curr=curr.sibling;
			}
			if(k) {
				TagNode ptr =curr.firstChild;
				while(ptr!=null) {
					if(ptr.tag.equals("td")) {
						TagNode newTag = new TagNode("b",ptr.firstChild,null);
						ptr.firstChild=newTag;
					}
					ptr=ptr.sibling;
				}
			}
			else {
				return;
			}
			
		}
		addBoldTag(n.firstChild,r);
		addBoldTag(n.sibling,r);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag)
	{
		if(tag.equals("p")||tag.equals("em")||tag.equals("b")||tag.equals("ol")||tag.equals("ul"))removeTag(root,tag);
		else return;
	}
			
	 private void removeTag(TagNode n, String tag){
		TagNode childSib;
		TagNode newSib;
		TagNode remove;
		if(n==null){
			return;
		}else if (n.sibling!=null&&n.sibling.tag.equals(tag)){
			remove=n.sibling;
			newSib=remove.sibling;
			n.sibling=remove.firstChild;
			childSib=remove.firstChild;
			if (tag.equals("ol")||tag.equals("ul")){
				while(childSib.sibling!=null){
					childSib.tag="p";
					childSib=childSib.sibling;
				}
				childSib.tag="p";
			}else{
				while(childSib.sibling!=null){
				     childSib=childSib.sibling;
				}
			}
			childSib.sibling=newSib;
		}else if (n.firstChild!=null&&n.firstChild.tag.equals(tag)){
			remove=n.firstChild;
			newSib=remove.sibling;
			childSib=remove.firstChild;
			if (tag.equals("ol")||tag.equals("ul")){
				while(childSib.sibling!=null){
					childSib.tag="p";
					childSib=childSib.sibling;
				}
				childSib.tag="p";
			}else{
				while(childSib.sibling!=null){
					childSib=childSib.sibling;
				}
			}
			childSib.sibling=newSib;
			n.firstChild=remove.firstChild;
		}
		removeTag(n.sibling,tag);
		removeTag(n.firstChild,tag);
		
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		if(tag.equals("") || word.equals("") || word == null || tag == null) {
			return;
		}
		if(tag.equals("em") || tag.equals("b")) {
			recur(word, tag, root.firstChild);
		}
	}
	private TagNode recursiveAddTag(String str, String tag, String target) {
		if(str.equals("") || str == null) {
			return new TagNode(null, null, null);
		}
		int num = str.toLowerCase().indexOf(target.toLowerCase()); 
		boolean right = rightIndex(str, target, num);
		boolean c = true; 
		while( (num == 0 || (num > 0 && (str.charAt(num - 1) != ' ' || c))) && right == false) {
			num = str.toLowerCase().indexOf(target.toLowerCase(), num + target.length());
			right = rightIndex(str, target, num);
			if(right == false) {
				c = true;
			}
		}
		if(num == 0) {
			int len = target.length();
			if(str.length() > len && (str.charAt(len) == '?' || str.charAt(len) == '.' || 
					str.charAt(len) == ':' || str.charAt(len) == ',' || str.charAt(len) == ';' || str.charAt(len) == '!')) {
				TagNode tagN = new TagNode(tag, null, null);
				tagN.firstChild = new TagNode(str.substring(num, len + 1), null, null);
				tagN.sibling = recursiveAddTag(str.substring(len + 1), tag, target); 
				return tagN;
			}else{
				TagNode tagN = new TagNode(tag, null, null);
				tagN.firstChild = new TagNode(str.substring(num, len), null, null);
				if(len == str.length()) {
					tagN.sibling = recursiveAddTag("", tag, target); 
				}else {
					tagN.sibling = recursiveAddTag(str.substring(len), tag, target); 
				} 
				return tagN;
			}
			
		}else if(num == -1) {
			TagNode tagN = new TagNode(str, null, null);
			return tagN;
		}else {
			TagNode tagN = new TagNode(str.substring(0, num), null, null);
			tagN.sibling = recursiveAddTag(str.substring(num), tag, target); 
			return tagN;
		}
	}
	private void recur(String wtc, String tag, TagNode root) {
		if(root == null) {
			return;
		}
		TagNode rootSib = root;
		TagNode rootS = root.sibling;
		if(root.firstChild != null && root.firstChild.firstChild == null) {
			TagNode P = root;
			TagNode temp = root.firstChild.sibling;
			root = recursiveAddTag(root.firstChild.tag, tag, wtc);
			P.firstChild = root;
			TagNode pr = null;
			TagNode ptr = root;
			while(ptr.sibling != null) {
				pr = ptr;
				ptr = ptr.sibling;
			}
			if(pr != null && pr.sibling != null && pr.sibling.tag == null) {
				pr.sibling = temp;
				root = pr;
			}else {
				ptr.sibling = temp;
				root = ptr;
			}
			recur(wtc, tag, P.sibling);
		}
		if(rootS != null && rootS.firstChild == null) {
			TagNode P = rootSib;
			TagNode sTemp = rootS.sibling;
			rootS = recursiveAddTag(rootS.tag, tag, wtc);
			P.sibling = rootS;
			TagNode pr = null;
			TagNode ptr = rootS;
			while(ptr.sibling != null) {
				pr = ptr;
				ptr = ptr.sibling;
			}
			if(pr != null && pr.sibling != null && pr.sibling.tag == null) {
				pr.sibling = sTemp;
				rootSib = pr;
			}else {
				ptr.sibling = sTemp;
				rootSib = ptr;
			}
		}
		recur(wtc, tag, root.firstChild);
		recur(wtc, tag, root.sibling);
	}
	private boolean pun(char c) {
		if(c == '!' || c == '?' || c == '.' || c == ',' || c == ';' || c == ':') {
			return true;
		}
		return false;
	}
	private boolean rightIndex(String s, String word, int ind) {
		if(s.length() == ind + word.length() || ((s.length() > ind + word.length()) 
				&& (s.charAt(ind + word.length()) == ' '))) {
			if(ind > 0 && s.charAt(ind - 1) == ' ') {
				return true;
			}else if(ind == 0) {
				return true;
			}
		}
		if((s.length() >= ind + word.length() + 1 && pun(s.charAt(ind + word.length())))
				&& ((s.length() >= ind + word.length() + 2 && 
				s.charAt(ind + word.length() + 1) == ' ') || s.length() == ind + word.length() + 1)) {
			if(ind > 0 && s.charAt(ind - 1) == ' ') {
				return true;
			}else if(ind == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
