package com.company;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import nf.co.mohatram.hidingdot.game.LevelProperty;
import nf.co.mohatram.hidingdot.game.ObjectProperty;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class HidingDotLevelGenerator {
    public static final int base_width = 80;//all the images should have width
    public static final int base_height = 48;//and height that are multiple of base_width and base_height

    public static final int player_color = 0xffff00ff;
    public static final int star_color = 0xffffff00;
    public static final int visible_color = 0xff000000;
    public static final int friendly_color = 0xff00ff00;
    public static final int deadly_color = 0xffff0000;
    public static final int finish_color = 0xff0000ff;

    public static int no_of_stars_currently_found = 0;

    public static final int base_factor = 10;
    private static int no_of_end_boxes_found = 0;
    private static int no_of_visble_boxes_found = 0;
    private static int no_of_green_boxes_found = 0;
    private static int no_of_red_boxes_found = 0;


    public static void main(String[] args) throws Exception {
        try {
            String imageName = args[0];
            String levelName = args[1];

            System.out.println("Generating level from " + imageName);
            File imageFile = new File(imageName);
            if (!imageFile.exists()) {
                throw new Exception("File " + imageName + " doesn\'t exists");
            }

            BufferedImage bufferedImage = ImageIO.read(imageFile);
            int imageHeight = bufferedImage.getHeight();
            int imageWidth = bufferedImage.getWidth();

            if (imageHeight % base_height != 0 || imageWidth % base_width != 0) {
                throw new Exception("Image should be of dimension 80x48 or the multiple of 80x48");
            }


            File levelFile = new File(levelName);
            if (!levelFile.exists()) levelFile.createNewFile();
            FileOutputStream levelFileOutputStream = new FileOutputStream(levelFile);


            //generate level
            LevelProperty levelProperty = new LevelProperty();
            levelProperty.levelWidth = imageWidth * base_factor;
            levelProperty.levelHeight = imageHeight * base_factor;
            levelProperty.breakRow = imageHeight / base_height;
            levelProperty.breakCol = imageWidth / base_width;

            for (int j = 0; j < imageHeight; j++) {
                for (int i = 0; i < imageWidth; i++) {
                    int pixel = bufferedImage.getRGB(i, j);
                    //System.out.print(Integer.toHexString(pixel)+ " ");
                    switch (pixel) {
                        case 0xffffffff: {
                            //found white nothing to do
                            break;
                        }
                        case friendly_color: {
                            //found friendly barrier
                            boolean alreadyFound = false;
                            for (ObjectProperty objectProperty : levelProperty.friendlyBarriersProperties) {
                                if (objectProperty.getBoundingRectangle().contains(i * base_factor + 1, j * base_factor + 1)) {
                                    alreadyFound = true;
                                    break;
                                }
                            }
                            if (!alreadyFound) {
                                ObjectProperty objectProperty = new ObjectProperty();
                                int w = 1, h = 1;

                                try {
                                    while (true) {
                                        if (bufferedImage.getRGB(i + w, j) != pixel) {
                                            break;
                                        } else {
                                            w++;
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }

                                    try {
                                        while (true) {
                                            if (bufferedImage.getRGB(i, j + h) != pixel) {
                                                break;
                                            } else {
                                                h++;
                                            }
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                    }

                                //optimize width
                                for (int oj=1; oj<h; oj++){
                                    for (int oi=1; oi<w; oi++){
                                        try {
                                            if (bufferedImage.getRGB(i + oi, j + oj) != pixel) {
                                                w = oi;
                                                break;
                                            }
                                        }catch (ArrayIndexOutOfBoundsException e){}
                                    }
                                }
                                no_of_green_boxes_found++;
                                objectProperty.dimension.set(w * base_factor, h * base_factor);
                                objectProperty.position.set(i * base_factor, j * base_factor);
                                levelProperty.friendlyBarriersProperties.add(objectProperty);
                            }
                            break;
                        }
                        case deadly_color: {
                            //found deadly barrier
                            boolean alreadyFound = false;
                            for (ObjectProperty objectProperty : levelProperty.deadlyBarriersProperties) {
                                if (objectProperty.getBoundingRectangle().contains(i * base_factor + 1, j * base_factor + 1)) {
                                    alreadyFound = true;
                                    break;
                                }
                            }
                            if (!alreadyFound) {
                                ObjectProperty objectProperty = new ObjectProperty();
                                int w = 1, h = 1;

                                //move along width
                                try {
                                    while (true) {
                                        if (bufferedImage.getRGB(i + w, j) != pixel) {
                                            break;
                                        } else {
                                            w++;
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }


                                //move along height

                                    try {
                                        while (true) {
                                            if (bufferedImage.getRGB(i, j + h) != pixel) {
                                                break;
                                            } else {
                                                h++;
                                            }
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                    }


                                //optimize width
                                for (int oj=1; oj<h; oj++){
                                    for (int oi=1; oi<w; oi++){
                                        try {
                                            if (bufferedImage.getRGB(i + oi, j + oj) != pixel) {
                                                w = oi;
                                                break;
                                            }
                                        }catch (ArrayIndexOutOfBoundsException e){}
                                    }
                                }

                                no_of_red_boxes_found++;
                                objectProperty.dimension.set(w * base_factor, h * base_factor);
                                objectProperty.position.set(i * base_factor, j * base_factor);
                                levelProperty.deadlyBarriersProperties.add(objectProperty);
                            }
                            break;
                        }
                        case finish_color: {
                            //found ending box
                            if (levelProperty.finishPointAreaProperty == null) {
                                int w = 1;
                                int h = 1;

                                //move along width
                                try {
                                    while (true) {
                                        if (bufferedImage.getRGB(i + w, j) != pixel) {
                                            break;
                                        } else {
                                            w++;
                                            //System.out.println(w+" Width");
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    //System.out.println(e.toString());
                                }

                                //move along height
                                try {
                                    while (true) {
                                        if (bufferedImage.getRGB(i, j + h) != pixel) {
                                            break;
                                        } else {
                                            h++;
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    //System.out.println(e.toString());
                                }

                                ObjectProperty objectProperty = new ObjectProperty();
                                objectProperty.dimension.set(w * base_factor, h * base_factor);
                                objectProperty.position.set(i * base_factor, j * base_factor);
                                levelProperty.finishPointAreaProperty = objectProperty;
                                no_of_end_boxes_found++;
                            }
                            break;
                        }
                        case visible_color: {
                            //found visible areas
                            boolean alreadyFound = false;
                            for (ObjectProperty objectProperty : levelProperty.visibleAreaProperties) {
                                if (objectProperty.getBoundingRectangle().contains(i * base_factor + 1, j * base_factor + 1)) {
                                    alreadyFound = true;
                                    break;
                                }
                            }
                            if (!alreadyFound) {
                                ObjectProperty objectProperty = new ObjectProperty();
                                int w = 1, h = 1;

                                //move along width
                                try {
                                    while (true) {
                                        if (bufferedImage.getRGB(i + w, j) != pixel && bufferedImage.getRGB(i + w, j) != player_color && bufferedImage.getRGB(i + w, j) != star_color) {
                                            break;
                                        } else {
                                            w++;
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }


                                //move along height
                                    try {
                                        while (true) {
                                            if (bufferedImage.getRGB(i, j + h) != pixel && bufferedImage.getRGB(i, j + h) != player_color && bufferedImage.getRGB(i, j + h) != star_color) {
                                                break;
                                            } else {
                                                h++;
                                            }
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                    }


                                //optimize width
                                for (int oj=1; oj<h; oj++){
                                    for (int oi=1; oi<w; oi++){
                                        try {
                                            if (bufferedImage.getRGB(i + oi, j + oj) != pixel &&
                                                    bufferedImage.getRGB(i+oi, j + oj) != player_color &&
                                                    bufferedImage.getRGB(i+oi, j + oj) != star_color) {
                                                w = oi;
                                                break;
                                            }
                                        }catch (ArrayIndexOutOfBoundsException e){}
                                    }
                                }

                                no_of_visble_boxes_found++;
                                objectProperty.dimension.set(w * base_factor, h * base_factor);
                                objectProperty.position.set(i * base_factor, j * base_factor);
                                levelProperty.visibleAreaProperties.add(objectProperty);
                            }
                            break;
                        }
                        case star_color: {
                            //star position found
                            boolean alreadyFound = false;
                            for (ObjectProperty objectProperty : levelProperty.starsProperties) {
                                if (objectProperty.getBoundingRectangle().contains(i * base_factor + 1, j * base_factor + 1)) {
                                    alreadyFound = true;
                                    break;
                                }
                            }
                            if (!alreadyFound) {
                                no_of_stars_currently_found++;
                                if (no_of_stars_currently_found > 2) {
                                    throw new Exception("More than 3 stars found.");
                                } else {
                                    ObjectProperty objectProperty = new ObjectProperty();

                                    //move along width

                                    objectProperty.dimension.set(2.5f * base_factor, 2.5f * base_factor);
                                    objectProperty.position.set(i * base_factor, j * base_factor);
                                    levelProperty.starsProperties.add(objectProperty);
                                }
                            }
                            break;
                        }
                        case player_color: {
                            //player initial location found
                            if (levelProperty.playerProperty == null) {
                                ObjectProperty objectProperty = new ObjectProperty();
                                objectProperty.position.set(i * base_factor, j * base_factor);
                                objectProperty.dimension.set(2.1f * base_factor, 2.1f * base_factor);
                                levelProperty.playerProperty = objectProperty;
                            } else {
                                throw new Exception("The number of players is invalid in "+imageName+"\nExpected 1 found more than 1");
                            }
                            break;
                        }
                        default: {
                            //unknown color
                        }

                    }
                    //System.out.println(i + "," + j);
                }
            }
            if (no_of_stars_currently_found != 2) {
                throw new Exception("The no of stars are invalid in " + imageName + "\n Expected 2 Found " + no_of_stars_currently_found);
            }

            if (no_of_end_boxes_found != 1) {
                throw new Exception("The no of end boxes are invalid in " + imageName + "\n Expected 1 Found " + no_of_end_boxes_found);
            }
            if(levelProperty.playerProperty==null){
                throw new Exception("Player position not defined in "+imageName);
            }
            System.out.println("No of visible boxes found:" + no_of_visble_boxes_found);
            System.out.println("No of red boxes found:" + no_of_red_boxes_found);
            System.out.println("No of green boxes found:" + no_of_green_boxes_found);
            System.out.println("");
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            String toJson = json.toJson(levelProperty);
            levelFileOutputStream.write(toJson.getBytes());

        } catch (Exception e) {
            System.out.println("Invalid arguments.");
            System.out.println("Usage " + HidingDotLevelGenerator.class.getName() + "[filename.png] [levelname.json] [levelmap.json]");
            e.printStackTrace();
        }

        System.gc();
    }

    /*public static void printRect(Rectangle rectangle) {
        System.out.println(rectangle.toString());
    }*/
}
