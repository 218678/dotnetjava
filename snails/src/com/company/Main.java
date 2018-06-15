package com.company;

import java.util.ArrayList;
import java.util.Random;
import com.company.AppFrame;

import javax.swing.*;

public class Main {
    public static final int MAP_SIZE = 4;
    public static final int GRASS_NUM = 2;
    public static final int SNAIL_NUM = 8;
    public static Random gen = new Random();
    public static Map map;

    public static void main(String[] args) throws InterruptedException {
        AppFrame frame = new AppFrame(800,600,MAP_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Snails");
        frame.setVisible(true);

        map =  new Map(MAP_SIZE, GRASS_NUM, SNAIL_NUM, frame.getFields());

        for (int i=0;i<frame.fields.length; i++) {
            for (int j=0;j<frame.fields[i].length; j++) {
                map.fields[i][j].setHeight(gen.nextInt(6));
            }
        }

        map.startGrass();
        Thread.sleep(2000);
        map.startSnails();

//        Thread.sleep(10000);
//        map.stopGrass();
//        Thread.sleep(5000);
//        map.stopSnails();

        map.joinGrass();
        map.joinSnails();
    }
}

class Map {
    public final int map_size;
    public final int snail_num;
    public final int grass_num;

    public FieldModel[][] fields;
    public Grass[] grass;
    public Snail[] snails;

    public Map(int map_size_, int grass_num_, int snail_num_, FieldModel[][] fields_) {
        map_size = map_size_;
        grass_num = grass_num_;
        snail_num = snail_num_;

        fields = fields_;
        grass = new Grass[grass_num];
        snails = new Snail[snail_num];

        for (int i=0; i<grass.length; i++) {
            grass[i] = new Grass("grass_"+Integer.toString(i));
        }

        for (int i=0; i<snails.length; i++) {
            snails[i] = new Snail("snail_"+Integer.toString(i));

        }
    }

    public void printMap() {
        for (int i = 0; i< map_size; i++) {
            for(int j = 0; j< map_size; j++) {
                System.out.print(fields[i][j].toString() + " ");
            }
            System.out.println();
        }
        System.out.println(new String(new char[map_size]).replace("\0", "-------------------"));
    }

    public void startGrass() {
        for (int i=0; i<grass.length; i++) {
            grass[i].start();
        }
    }

    public void stopGrass() {
        for (Grass g:grass) {
            g.stopFlag = true;
        }
    }

    public void startSnails() {
        for (int i=0; i<snails.length; i++) {
            snails[i].start();
        }
    }

    public void stopSnails() {
        for (Snail s:snails) {
            s.stopFlag = true;
        }
    }

    public void joinSnails() {
        for (int i=0; i<snails.length; i++) {
            snails[i].join();
        }
    }

    public void joinGrass() {
        for (int i=0; i<grass.length; i++) {
            grass[i].join();
        }
    }

    public FieldModel getUnoccupiedField() {
        ArrayList<FieldModel> array = new ArrayList<FieldModel>();

        for (int i = 0; i < map_size; i++) {
            for (int j = 0; j < map_size; j++) {
                if (!fields[i][j].isOccupied()) {
                    array.add(fields[i][j]);
                }
            }
        }
        return array.get(Main.gen.nextInt(array.size()));
    }
}

class Grass implements Runnable {
    private Thread thread;
    private String threadName;
    private int id;
    private static int nextId = 1;
    private Random gen = new Random();
    public boolean stopFlag = false;

    Grass(String name) {
        id = nextId++;
        threadName = name;
        System.out.println("[LOG] Created Grass: " + this.toString());
    }

    public void run() {
        int tick = 0;
        int tickFail = 0;
        System.out.println("[LOG] Running Grass: " + this.toString());
        while(!stopFlag) {
            try {
                FieldModel field = Main.map.fields[gen.nextInt(Main.map.map_size)][gen.nextInt(Main.map.map_size)];
                if (!field.isOccupied() && field.getGrassHeight() < 6) {
                    tickFail = 0;
                    int h1 = field.getGrassHeight();
                    field.growGrass();
                    int h2 = field.getGrassHeight();
                    System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", GROW " + h1 + "->" + h2);
                    Thread.sleep((gen.nextInt(10)*100 + 3000)/2);
                } else {
                    ++tickFail;
                    System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", FAIL tickFail: " + tickFail);
                    Thread.sleep((gen.nextInt(10)*100)/2);
//                    if (tickFail == 3)
//                        stopFlag = true;
                }
            } catch (InterruptedException e) {
                System.out.println("[LOG] Thread Grass: " + this.toString() + " Interrupted");
            }
            tick++;
        }
        System.out.println("[LOG] Exit Grass: " + this.toString());
    }

    public void start(){
        System.out.println("[LOG] Start: " + this.toString());
        if (thread == null){
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    public void join(){
        try {
            thread.join();
            System.out.println("[LOG] Join: " + this.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
//        return "[threadName " + threadName + ", id " + id + "]";
        return threadName;
    }
}

class Snail implements Runnable {
    private Thread thread;
    private String threadName;
    private int id;
    private static int nextId = 1;
    private Random gen = new Random();
    public boolean stopFlag = false;
    private FieldModel lastField;

    Snail(String name) {
        id = nextId++;
        threadName = name;
        System.out.println("[LOG] Created Snail: " + this.toString());
    }

    public void run() {
        int tick = 0;
        int hunger = 0;
        System.out.println("[LOG] Running Snail: " + this.toString());

        lastField = Main.map.getUnoccupiedField();
        if (lastField == null){
            System.out.println("[LOG] Thread Snail: " + this.toString() + " All fields taken.");
            return;
        }
        lastField.placeSnail(id);

        while(!stopFlag) {
            try {
                FieldModel field = Main.map.fields[gen.nextInt(Main.map.map_size)][gen.nextInt(Main.map.map_size)];

                if (lastField == null) {
                    field.placeSnail(id);
                    lastField = field;
                } else if (lastField.getGrassHeight()>0) {
                    int h1 = lastField.getGrassHeight();
                    if (lastField.eatGrass()) {
                        if (hunger > 1) {
                            hunger -= 2;
                        }
                        else {
                            hunger = 0;
                        }
                        int h2 = lastField.getGrassHeight();
                        System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", hunger: " + hunger + ", EAT " + h1 + "->" + h2);
                    }
                    else {
                        hunger++;
                        System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", hunger: " + hunger);
                    }
                    Thread.sleep((gen.nextInt(10)*100 + 5000)/2);
                } else if (!field.isOccupied()) { // && field.getGrassHeight() > 0) {
                    lastField.removeSnail();
                    field.placeSnail(id);
                    Thread.sleep((gen.nextInt(1)*100+1500)/2);

                    int h1 = field.getGrassHeight();
                    if (field.eatGrass()) {
                        if (hunger > 1)
                            hunger -= 2;
                        else
                            hunger = 0;
                        int h2 = field.getGrassHeight();
                        System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", hunger: " + hunger + ", EAT " + h1 + "->" + h2);
                    }
                    else {
                        hunger++;
                        System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", hunger: " + hunger);
                    }
                    Thread.sleep((gen.nextInt(10)*100 + 5000)/2);
                    lastField = field;
                } else {
//                    lastField.placeSnail(id);
                    hunger++;
                    System.out.println("[LOG] " + this.toString() + ", tick: " + tick + ", hunger: " + hunger);
                    Thread.sleep((gen.nextInt(10) * 100 + 5000) / 2);
                }
            } catch (InterruptedException e) {
                System.out.println("[LOG] Thread Snail: " + this.toString() + " Interrupted");
            }
            if (hunger >= 5) {
                break;
            }
            tick++;
        }
        lastField.removeSnail();
        System.out.println("[LOG] Exit Snail: " + this.toString() + ", STARVED");
    }

    public void start(){
        System.out.println("[LOG] Start: " + this.toString());
        if (thread == null){
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    public void join(){
        try {
            thread.join();
            System.out.println("[LOG] Join: " + this.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
//        return "[threadName " + threadName + ", id " + id + "]";
        return threadName;
    }
}