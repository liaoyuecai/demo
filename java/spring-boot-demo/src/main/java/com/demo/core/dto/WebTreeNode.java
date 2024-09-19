package com.demo.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WebTreeNode {
    protected Integer id;
    protected Object key;
    protected Object parentKey;
    protected String title;
    protected Object value;
    protected boolean isLeaf = false;
    protected boolean disabled = false;
    protected List<WebTreeNode> children;

    public void addChild(WebTreeNode node) {
        if (children == null)
            children = new ArrayList<>();
        children.add(node);
    }

    public void addChild(int index, WebTreeNode node) {
        if (children == null)
            children = new ArrayList<>();
        children.add(index, node);
    }

}
