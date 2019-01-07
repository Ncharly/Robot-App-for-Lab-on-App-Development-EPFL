package com.example.kandels.myapplication;

public class Node {


    public int[] Index = new int[3];
    public boolean IsWalkable;
    public float G;
    public float H;
    public float F;
    public int State;
    public Node ParentNode;

    static final int OPEN = 0;
    static final int CLOSED = 1;
    static final int NOT_TESTED = 2;

    public Node(int index, int index_x, int index_y) {
        Index[0] = index;
        Index[1] = index_x;
        Index[2] = index_y;
        IsWalkable = false;
        G = 0;
        H = 0;
        getF();
        setState(CLOSED);
        ParentNode = null;
    }

    boolean getG_H_F( Node node_ini, Node node_fin){
        boolean change_parent = true;
        if(State == NOT_TESTED){
            G = Math.abs(Index[1] - node_ini.Index[1]) + Math.abs(Index[2] - node_ini.Index[2]);
            H = Math.abs(Index[1] - node_fin.Index[1]) + Math.abs(Index[2] - node_fin.Index[2]);
            getF();

        }else if(State == OPEN){
            float G_new = Math.abs(Index[1] - node_ini.Index[1]) + Math.abs(Index[2] - node_ini.Index[2]);
            if(G_new < G){
                G = G_new;
            }else{
                change_parent = false;
            }
        }
        return change_parent;
    }


    void getF(){
        F = G + H;
    }

    void setState(int state){
        State = state;
    }
}
