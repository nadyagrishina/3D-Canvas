package com.nadyagrishina.canvas3d;

import javax.swing.*;

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600));
    }
}
