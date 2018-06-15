package com.company;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AppFrame extends JFrame {
    private static int DEFAULT_WIDTH;
    private static int DEFAULT_HEIGHT;

    JPanel panel;
    FieldModel[][] fields;

    AppFrame(int window_width, int window_height, int map_size) {
        DEFAULT_HEIGHT = window_height;
        DEFAULT_WIDTH = window_width;
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        panel = new JPanel(new GridLayout(map_size,map_size));
        fields = new FieldModel[map_size][map_size];

        for (int i=0;i<fields.length; i++) {
            for (int j=0;j<fields[i].length; j++) {

                fields[j][i] = new FieldModel();
                panel.add(fields[j][i]);
            }
        }

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ev) {
//                Component c = (Component)ev.getSource();
                synchronized (this) {
                    for (int i=0;i<fields.length; i++) {
                        for (int j = 0; j < fields[i].length; j++) {
                            fields[j][i].resizeIcon();
                        }
                    }
                }
            }
        });

        add(panel);
    }

    void setFieldHeight(int x, int y, int h) {
        fields[x][y].setHeight(h);
    }

    FieldModel[][] getFields() {
        return fields;
    }
}

class FieldModel extends JButton {
    private int grass_height;
    private int present_snail_id;
    private ImageIcon imgic;

    FieldModel() {
        grass_height = 0;
        present_snail_id = 0;
        imgic = new ImageIcon();
//        this.setEnabled(false);
        this.setFocusPainted(false);
        this.setBorder(new LineBorder(Color.white));
        this.setMargin(new Insets(0,0,0,0));
        this.setBackground(Color.gray);
    }

    void setHeight(int h) {
        grass_height = h;
        updateField();
    }

    void updateField() {
        Image img;

        int flag;
        if (present_snail_id == 0)
            flag = 0;
        else
            flag = 1;

        switch (Integer.toString(grass_height) + Integer.toString(flag)) {
            case "00":
                imgic = new ImageIcon("img/00.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "10":
                imgic = new ImageIcon("img/10.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "20":
                imgic = new ImageIcon("img/20.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "30":
                imgic = new ImageIcon("img/30.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "40":
                imgic = new ImageIcon("img/40.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "50":
                imgic = new ImageIcon("img/50.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "60":
                imgic = new ImageIcon("img/60.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;

            case "01":
                imgic = new ImageIcon("img/01.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "11":
                imgic = new ImageIcon("img/11.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "21":
                imgic = new ImageIcon("img/21.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "31":
                imgic = new ImageIcon("img/31.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "41":
                imgic = new ImageIcon("img/41.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "51":
                imgic = new ImageIcon("img/51.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;
            case "61":
                imgic = new ImageIcon("img/61.png");
                img = imgic.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
                this.setIcon(new ImageIcon(img));
                break;

            default:
                this.setIcon(new ImageIcon());
                this.setText("x");
                break;
        }
    }

    boolean isOccupied() {
        return present_snail_id != 0;
    }

    boolean placeSnail(int id) {
        synchronized (this) {
            if (isOccupied())
                return false;
            else {
                present_snail_id = id;
                updateField();
                return true;
            }
        }
    }

    boolean growGrass() {
        synchronized (this) {
            if (grass_height < 6) {
                grass_height++;
                updateField();
                return true;
            } else
                return false;
        }
    }

    boolean eatGrass() {
        synchronized (this) {
            if (grass_height > 0) {
                grass_height--;
                updateField();
                return true;
            } else
                return false;
        }
    }

    @Override
    public String toString() {
        return "[Grass:" + grass_height + ", Snail:" + present_snail_id +"]";
    }

    public int getGrassHeight() {
        return grass_height;
    }

    void resizeIcon() {
        if (this.getIcon() != new JLabel().getIcon()){
            Image img = imgic.getImage().getScaledInstance(
                    this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
            this.setIcon(new ImageIcon(img));
        }
    }

    public void removeSnail() {
        synchronized (this) {
            present_snail_id = 0;
            updateField();
        }
    }
}