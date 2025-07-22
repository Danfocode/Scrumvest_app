package com.example.Scrumvest.util;

import com.example.Scrumvest.model.entity.HistoriaUsuario;
import java.util.List;

public class AVLTree {
    private class Node {
        Long key; // Usamos el ID de la historia como clave
        HistoriaUsuario value;
        Node left, right;
        int height;

        Node(Long key, HistoriaUsuario value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;

    // Obtener la altura de un nodo
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Obtener el factor de balance
    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Actualizar la altura de un nodo
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    // Rotación a la derecha
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    // Rotación a la izquierda
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Insertar una historia
    public void insert(Long key, HistoriaUsuario value) {
        root = insertRec(root, key, value);
    }

    private Node insertRec(Node node, Long key, HistoriaUsuario value) {
        if (node == null) {
            return new Node(key, value);
        }

        if (key < node.key) {
            node.left = insertRec(node.left, key, value);
        } else if (key > node.key) {
            node.right = insertRec(node.right, key, value);
        } else {
            node.value = value; // Actualizar si la clave ya existe
            return node;
        }

        updateHeight(node);
        int balance = balanceFactor(node);

        // Rotaciones para balancear
        if (balance > 1) {
            if (key < node.left.key) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }
        if (balance < -1) {
            if (key > node.right.key) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    // Eliminar una historia
    public void delete(Long key) {
        root = deleteRec(root, key);
    }

    private Node deleteRec(Node node, Long key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = deleteRec(node.left, key);
        } else if (key > node.key) {
            node.right = deleteRec(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                Node successor = getMinNode(node.right);
                node.key = successor.key;
                node.value = successor.value;
                node.right = deleteRec(node.right, successor.key);
            }
        }

        updateHeight(node);
        int balance = balanceFactor(node);

        // Rotaciones para balancear
        if (balance > 1) {
            if (balanceFactor(node.left) >= 0) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }
        if (balance < -1) {
            if (balanceFactor(node.right) <= 0) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    // Obtener el nodo con la clave mínima
    private Node getMinNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Obtener una historia por ID
    public HistoriaUsuario get(Long key) {
        Node node = getRec(root, key);
        return node != null ? node.value : null;
    }

    private Node getRec(Node node, Long key) {
        if (node == null || node.key.equals(key)) {
            return node;
        }
        if (key < node.key) {
            return getRec(node.left, key);
        }
        return getRec(node.right, key);
    }

    // Obtener todas las historias en orden
    public void inOrder(List<HistoriaUsuario> result) {
        inOrderRec(root, result);
    }

    private void inOrderRec(Node node, List<HistoriaUsuario> result) {
        if (node != null) {
            inOrderRec(node.left, result);
            result.add(node.value);
            inOrderRec(node.right, result);
        }
    }
}