package com.nadyagrishina.canvas3d;

import javax.swing.*;
import java.awt.*;

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("Canvas3D Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setLayout(new BorderLayout());
            frame.add(new Canvas3D(800, 600), BorderLayout.CENTER);
            frame.add(Driver.createControlsPanel(), BorderLayout.EAST);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JScrollPane createControlsPanel() {
        final String[] columnNames = {"Key Combination", "Description"};
        final Object[][] rows = {
                { "W", "Move the camera forwards." },
                { "S", "Move the camera backwards." },
                { "A", "Move the camera left." },
                { "D", "Move the camera right." },
                { "Q", "Move the camera up." },
                { "E", "Move the camera down." },
                { "P", "Toggle the active figure." },
                { "C", "Reset figure position." },
                { "R + Mouse", "Rotate the figure." },
                { "T + Mouse", "Scale ths figure." },
                { "SHIFT + Mouse", "Translate the figure." },
                { "M", "Toggle display of the axes." },
                { "N", "Toggle display of the cubic lines." },
                { "SPACE", "Toggle projection." },
                { "1", "Toggle display of the octahedron." },
                { "2", "Toggle display of the pyramid." }
        };

        final JTable table = new JTable(rows, columnNames);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);

        return new JScrollPane(table);
    }
}
