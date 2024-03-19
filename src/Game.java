//BEREN ÜNVEREN 221101006
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
public class Game extends JFrame  implements KeyListener, MouseListener {
    public ArrayList<Enemy> enemies=new ArrayList<>();
    public ArrayList<Friend> friends=new ArrayList<>();
    public AirCraft tinkerbell=new AirCraft();
    public GamePanel gamePanel = new GamePanel();
    public ArrayList<Fire> allFries = new ArrayList<>();
    boolean enemyInitialized = false;
    boolean gameContinues = true;

    public void callDispose(boolean situation){
        dispose();
        JFrame theEnd = new JFrame();
        theEnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theEnd.setSize(600,100);
        theEnd.setResizable(false);
        if(situation){
            theEnd.add(new JLabel("Oyunu kaybettiniz"));
        }
        else{
            theEnd.add(new JLabel("Oyunu kazandiniz"));
        }
        theEnd.setBackground(Color.white);
        theEnd.setVisible(true);
        theEnd.setTitle("(eger bastan burasi acildiysa bir kere daha sans vermenizi arz ediyorum)");
    }

    public Game(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,500);
        this.setResizable(false);
        this.add(gamePanel);
        this.setBackground(Color.white);
        this.setVisible(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setTitle("yeteri kadar hizli degil, umarim yine de kabul edersiniz");
    }

    public class GamePanel extends JPanel{
        public GamePanel(){}
        public void overlap(){
            ArrayList <Enemy> enemyCopy = new ArrayList<>(enemies);
            ArrayList <Friend> friendCopy = new ArrayList<>(friends);

            for(int i = 0; i<enemyCopy.size(); i++){
                for(int j = 0; j<friendCopy.size(); j++){
                    if(enemyCopy.get(i)!=null && friendCopy.get(j)!=null){
                        int firstX = enemyCopy.get(i).coordinateX; int firstY=enemyCopy.get(i).coordinateY;
                        int secondX = friendCopy.get(j).coordinateX; int secondY=friendCopy.get(j).coordinateY;
                        boolean sutun = firstX==secondX && Math.abs(firstY-secondY)==10;
                        boolean satir = firstY==secondY && Math.abs(firstX-secondX)==10;
                        if(sutun || satir){
                            enemyCopy.get(i).isalive=false; friendCopy.get(j).isalive=false;
                        }
                    }
                }
                if(enemyCopy.get(i)!=null && tinkerbell!=null){
                    int firstX = enemyCopy.get(i).coordinateX; int firstY=enemyCopy.get(i).coordinateY;
                    int secondX = tinkerbell.coordinateX; int secondY=tinkerbell.coordinateY;
                    boolean sutun = firstX==secondX && Math.abs(firstY-secondY)==10;
                    boolean satir = firstY==secondY && Math.abs(firstX-secondX)==10;
                    if(sutun || satir){
                        enemyCopy.get(i).isalive=false; tinkerbell.isalive=false;
                    }
                }
            }
        }

        public boolean areAllEnemiesDead(){
            ArrayList<Enemy>enemyCopy=new ArrayList<>(enemies);
            for(Enemy e : enemyCopy){
                if(enemyInitialized && e!=null && e.isalive) return false;
            }
            return true;
        }

        public void paintComponent(Graphics g) {
            overlap();
            super.paintComponent(g);

            ArrayList<Fire> copyOfAllFries = new ArrayList<>(allFries);

            for (Fire f : copyOfAllFries) {
                if (f != null && f.isTinkerbell) g.setColor(new Color(208, 108, 38));
                else if (f != null && f.isGoodSide) g.setColor(new Color(126, 77, 173));
                else if (f != null && !f.isGoodSide) g.setColor(new Color(19, 106, 134));

                if (f != null && f.Fire_x<480 && f.Fire_x>=0) g.fillRect(f.Fire_x, f.Fire_y, 5, 5);
            }

            for (Enemy e : enemies) {
                g.setColor(Color.BLACK);
                g.fillRect(e.coordinateX, e.coordinateY, 10, 10);
            }

            ArrayList<Friend>madeJustForException=new ArrayList<Friend>(friends);
            for (Friend f : madeJustForException) {
                g.setColor(new Color(66, 141, 59));
                g.fillRect(f.coordinateX, f.coordinateY, 10, 10);
            }

            g.setColor(Color.RED);
            if(tinkerbell!=null) g.fillRect(tinkerbell.coordinateX, tinkerbell.coordinateY, 10, 10);

            if(tinkerbell==null || !tinkerbell.isalive){
                gameContinues=false;
                callDispose(true);
            }
            if(enemyInitialized && areAllEnemiesDead()){
                gameContinues=false;
                callDispose(false);
            }
        }
    }

    public class Enemy extends Thread{
        int coordinateX, coordinateY;
        boolean isalive = true;
        Enemy() {
            enemyInitialized=true;
            enemies.add(this);
            initialCoordinate();
        }

        public void run(){
            while(isalive && gameContinues){
                Point temp= givePoint(coordinateX,coordinateY);
                coordinateX=temp.x; coordinateY= temp.y;
                gamePanel.repaint();
                try {sleep(500);}
                catch (Exception e) {e.printStackTrace();}

                temp= givePoint(coordinateX,coordinateY);
                coordinateX=temp.x; coordinateY= temp.y;
                gamePanel.repaint();
                try {sleep(500);}
                catch (Exception e) {e.printStackTrace();}

                Fire left = new Fire(false,true, false,coordinateX-5,coordinateY);
                Fire right = new Fire(false,false, false,coordinateX+10,coordinateY);
                allFries.add(left); allFries.add(right);
                left.start();right.start();
                gamePanel.repaint();


            }
            enemies.remove(this);
            gamePanel.repaint();
        }

        public void initialCoordinate(){
            Random r = new Random();
            boolean someoneHere = false;
            boolean initialized = false;
            while(!initialized){
                int randomx = (int)(r.nextInt(47))*10;
                int randomy = (int)(r.nextInt(47))*10;
                for(Enemy e : enemies){
                    if(e.coordinateX==randomx && e.coordinateY==randomy) someoneHere = true;
                }
                for(Friend f : friends){
                    if(f.coordinateX==randomx && f.coordinateY==randomy) someoneHere = true;
                }
                if(tinkerbell.coordinateX==randomx && tinkerbell.coordinateY==randomy) someoneHere = true;
                if(!someoneHere){
                    this.coordinateX=randomx; this.coordinateY=randomy;
                    initialized=true;
                }
            }
        }
    }

    public class Friend extends Thread{
        int coordinateX, coordinateY;
        Friend(){
            friends.add(this);
            initialCoordinate();
        }
        boolean isalive = true;

        public void run(){
            while(isalive && gameContinues){
                Point temp= givePoint(coordinateX,coordinateY);
                coordinateX=temp.x; coordinateY= temp.y;
                gamePanel.repaint();
                try {sleep(500);}
                catch (Exception e) {e.printStackTrace();}

                temp= givePoint(coordinateX,coordinateY);
                coordinateX=temp.x; coordinateY= temp.y;
                gamePanel.repaint();
                try {sleep(500);}
                catch (Exception e) {e.printStackTrace();}

                Fire left = new Fire(false,true, true,coordinateX-5,coordinateY);
                Fire right = new Fire(false,false, true,coordinateX+10,coordinateY);
                allFries.add(left); allFries.add(right);
                left.start();right.start();
                gamePanel.repaint();
            }
            friends.remove(this);
            gamePanel.repaint();
        }

        public void initialCoordinate(){
            Random r = new Random();
            boolean someoneHere = false;
            boolean initialized = false;
            while(!initialized){
                int randomx = (int)(r.nextInt(47))*10;
                int randomy = (int)(r.nextInt(47))*10;
                for(Enemy e : enemies){
                    if(e.coordinateX==randomx && e.coordinateY==randomy) someoneHere = true;
                }
                for(Friend f : friends){
                    if(f.coordinateX==randomx && f.coordinateY==randomy) someoneHere = true;
                }
                if(tinkerbell.coordinateX==randomx && tinkerbell.coordinateY==randomy) someoneHere = true;
                if(!someoneHere){
                    this.coordinateX=randomx; this.coordinateY=randomy;
                    initialized=true;
                }
            }
        }
    }

    public class AirCraft extends Thread{
        int coordinateX=250, coordinateY=250;
        boolean isalive = true;
        AirCraft(){
            tinkerbell=this;
        }
        public void run(){
            while(isalive && gameContinues){
                gamePanel.repaint();
            }
            tinkerbell=null;
            gameContinues=false;
            callDispose(true);
        }
    }

    public class Fire extends Thread{
        boolean isTinkerbell, isLeftSide, isGoodSide;
        int Fire_x ; int Fire_y;

        Fire(boolean isTinkerbell,boolean isLeftSide, boolean isGoodSide, int Fire_x, int Fire_y){
            this.isTinkerbell=isTinkerbell;
            this.isLeftSide=isLeftSide;
            this.isGoodSide=isGoodSide;
            this.Fire_x=Fire_x;
            this.Fire_y=Fire_y;
        }

        public void run(){
            gamePanel.repaint();
            while(gameContinues && Fire_x>0 && Fire_x<475){
                gamePanel.repaint();
                if(isLeftSide) Fire_x-=10;
                else Fire_x+=10;

                ilkelVurus();

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                gamePanel.repaint();
            }
            allFries.remove(this);
            try {
                this.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        public void ilkelVurus(){
            ArrayList<Enemy>enemyCopy = new ArrayList<>(enemies);
            ArrayList<Friend>friendCopy = new ArrayList<>(friends);

            if(this.isGoodSide || this.isTinkerbell){
                for(Enemy e : enemyCopy){
                    if(e!=null && this.Fire_x>=e.coordinateX && this.Fire_x<=e.coordinateX+5
                            &&this.Fire_y>=e.coordinateY && this.Fire_y<=e.coordinateY+5){
                        e.isalive=false;
                    }
                }
            }
            else{
                for(Friend f : friendCopy){
                    if(f!=null && this.Fire_x>=f.coordinateX && this.Fire_x<=f.coordinateX+5
                            &&this.Fire_y>=f.coordinateY && this.Fire_y<=f.coordinateY+5){
                        f.isalive=false;
                    }
                }
                if(tinkerbell!=null && this.Fire_x>=tinkerbell.coordinateX && this.Fire_x<=tinkerbell.coordinateX+5
                        &&this.Fire_y>=tinkerbell.coordinateY && this.Fire_y<=tinkerbell.coordinateY+5){
                    tinkerbell.isalive=false;
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        gamePanel.repaint();
        if(tinkerbell!=null){
            switch(e.getKeyChar()){
                case 'w': {
                    if(tinkerbell.coordinateY!=0){
                        tinkerbell.coordinateY -=10;
                        gamePanel.repaint();
                    }
                    break;
                }
                case 'a': {
                    if(tinkerbell.coordinateX!=0){
                        tinkerbell.coordinateX -=10;
                        gamePanel.repaint();
                    }
                    break;
                }
                case 's': {
                    if(tinkerbell.coordinateY!=450){
                        tinkerbell.coordinateY +=10;
                        gamePanel.repaint();
                    }
                    break;
                }
                case 'd': {
                    if(tinkerbell.coordinateX!=470){
                        tinkerbell.coordinateX +=10;
                        gamePanel.repaint();
                    }
                    break;
                }
            }
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {
        gamePanel.repaint();
        if(tinkerbell!=null){
            Fire left = new Fire(true,true, true,tinkerbell.coordinateX-5,tinkerbell.coordinateY);
            Fire right = new Fire(true,false, true,tinkerbell.coordinateX+10,tinkerbell.coordinateY);
            allFries.add(left); allFries.add(right);
            left.start();right.start();
        }
        gamePanel.repaint();
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public Point givePoint(int x, int y){
        Random random = new Random();
        boolean successfulRandom= false;
        Point a=new Point(999,999);
        while(true) {
            int sans = (int)(random.nextInt(4))+1;
            switch (sans) {
                case 1: {
                    if (x + 10 >= 0 && x + 10 <= 470) {
                        return new Point(x + 10, y);
                    }
                    break;
                }
                case 2: {
                    if (x - 10 >= 0 && x - 10 <= 470) {
                        return new Point(x - 10, y);
                    }
                    break;
                }
                case 3: {
                    if (y + 10 >= 0 && y + 10 <= 450) {
                        return new Point(x, y + 10);
                    }
                    break;
                }
                case 4: {
                    if (y - 10 >= 0 && y - 10 <= 450) {
                        return new Point(x, y - 10);
                    }
                    break;
                }
            }
        }
    }
}

