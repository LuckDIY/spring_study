package com.study.spring_study.leetcode;

public class Demo {


    /**
     * 2095. 删除链表的中间节点
     * 解法：快慢指针确认中间节点，然后删除
     * @param head
     * @return
     */
    public ListNode deleteMiddle(ListNode head) {

        if(head.next==null){
            return null;
        }
        //1,3,4,7,1,2,6
        //确认中间节点
        ListNode preMid = head;
        ListNode one = head;
        ListNode two = head;

        while(two!=null){
            if(two.next==null){
                preMid.next = one.next;
                return head;
            }
            if(two.next.next==null){
                //删除中间节点
                one.next = one.next.next;
                return head;
            }

            preMid = one;
            one = one.next;
            two = two.next.next;
        }

        return null;

    }

    /**
     * 1234
     * 别人的优秀代码，牛逼简洁
     * @param head
     * @return
     */
    public ListNode deleteMiddle2(ListNode head) {
        //快慢指针
        ListNode slow = head,fast = head.next;
        if(fast == null) return null;
        while(fast.next != null && fast.next.next != null){
            slow = slow.next;
            fast = fast.next.next;
        }
        slow.next = slow.next.next;
        return head;
    }


    /**
     * 奇偶链表连接在一起
     *
     * @param head
     * @return
     */
    public ListNode oddEvenList(ListNode head) {
        if(head==null){
            return null;
        }

        ListNode odd = head,even = head.next;

        ListNode tailOdd = odd,headEven = even;

        if(even==null){
            return head;
        }

        while(even!=null && even.next!=null){
            //偶数的下一个节点不等于空

            //链表指向
            ListNode temp = tailOdd.next.next;
            tailOdd.next = temp;
            tailOdd = temp;

            //链表指向
            even.next = even.next.next;
            even = even.next;

        }


        tailOdd.next = headEven;

        return head;
    }


    /**
     * 206. 反转链表
     * 思路：变量1 变量2 变量3  三个变量翻转
     * @param head
     * @return
     */
    public static ListNode reverseList(ListNode head) {

        if(head==null){
            return null;
        }

        //1->2->3->4
        ListNode a = head,b = head.next;
        while(b!=null){

            //首先存储b的下一个节点
            ListNode c = b.next;

            //然后翻转
            b.next = a;

            //然后ab重新复制
            a = b;
            b = c;

        }
        head.next = null;
        return a;

    }

    /**
     * 递归解法
     * @param head
     * @return
     */
    public static ListNode reverseListByRecursion(ListNode head) {

        if(head==null){
            return null;
        }

        return recursion(head);
    }

    public static ListNode recursion(ListNode node){

        //如果节点的下一个节点为空则返回
        if (node.next==null) {
            return node;
        }

        //当前节点的下一个节点
        ListNode tailNode = recursion(node.next);

        //翻转，因为有方法栈记录顺序，不怕节点丢失
        node.next.next = node;

        //然后删掉节点的下一个节点，防止头节点粘连
        node.next = null;


        return tailNode;
    }


    public static void print(ListNode head){

        ListNode temp = head;

        while(temp!=null){
            System.out.print(temp.val+"->");
            temp = temp.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ListNode listNode = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, null))));
        print(listNode);


        //ListNode listNode1 = reverseList(listNode);
        ListNode recursion = reverseListByRecursion(listNode);
        print(recursion);
    }
}
