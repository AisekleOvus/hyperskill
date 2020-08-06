
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * AppendableSerialization is for cases, when you need to append serializable objects
 * to single file.
 * The Class has two constructors one of wich accepts String pathToFile and Object serializableObject,
 * another accepts File file and Object serializableObject
 * Method appendableObjectWriting(), actually, write your Object to File. It can write to empty file
 * or in file where some objects just had been written
 *
 */
class AppendableSerialization {
    private final String path;
    private final Object object;
    private final boolean appendable;
    private final File file;

    /**
     *
     * @param path - String path to file to save serializableObject
     * @param object - Object serializableObject
     */
    public AppendableSerialization(String path, Object object) {
        this.path = path;
        this.object = object;
        this.file = new File(path);
        this.appendable = isFileAppendable();
    }

    /**
     *
     * @param file - File descriptor of file to save serializableObject
     * @param object - Object serializableObject
     */
    public AppendableSerialization(File file, Object object) {
        this.file = file;
        this.path = file.getPath();
        this.object = object;
        this.appendable = isFileAppendable();
    }

    // Check if there are any 'objects' in file
    private boolean isFileAppendable() {
        if(file.exists() && !file.isDirectory()) {
            try(FileInputStream fis = new FileInputStream(path)) {
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);
                ois.readObject();
            } catch(Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isAppendable() { return appendable; }

    public boolean appendableObjectWriting() throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        byte[] output = baos.toByteArray();
        if(appendable) {
            output = Arrays.copyOfRange(output, 1, output.length);
            output[0] = 119;
            output[1] = 1;
            output[2] = 1;
        }
        if(file.length() == 0 || appendable) {
            FileOutputStream fos = new FileOutputStream(path,true);
            fos.write(output);
            fos.close();
            return true;
        }
        return false;
    }
}

class StringUtil {
    /* Applies Sha256 to a string and returns a hash. */
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String applySha256(Block block) {

        return  applySha256(new StringBuilder()
                .append(block.getId())
                .append(block.getMagicNumber())
                .append(block.getLastHash())
                .append(block.getTimeStamp())
                .toString());
    }
}

class Block implements Serializable {
    private int id;
    private String minerId;
    private Long startTimeStamp;
    private Integer magicNumber;
    private String lastHash;
    private String hash;
    private long creationTime;
    private Integer difficulty;
    private Integer difficultyOld;
    private String difficultyChange;
    private long serialVersionUID;

    public Block(String minerId, int id, Integer magicNumber, String lastHash,  String currHash, long startTimeStamp,
                 Integer difficulty, long serialVersionUID) {

        this.id = id;
        this.minerId = minerId;
        this.difficulty = difficulty;
        long finishTimeStamp = new Date().getTime();
        this.startTimeStamp = startTimeStamp;
        creationTime = (finishTimeStamp - startTimeStamp)/1000;
        // calculate difficulty for the next block
        this.difficultyOld = difficulty;
        if(creationTime < 15 && difficulty < 4) this.difficulty++;
        if(creationTime > 15 && this.difficulty > 0) this.difficulty--;

        difficultyChange = this.difficultyOld < this.difficulty ? "was increased to " + this.difficulty
                : this.difficultyOld > this.difficulty ? "was decreased by 1" :  "stays the same";
        this.magicNumber = magicNumber;
        this.lastHash = lastHash;
        this.hash = currHash;
        this.serialVersionUID = serialVersionUID;
    }

    @Override
    public String toString() {
        return  String.format("\nBlock:\nCreated by miner %s\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s\nHash of the block:"+"\n%s\nBlock was generating for %d seconds\nN %s", minerId, id, startTimeStamp, magicNumber,lastHash,hash, creationTime, difficultyChange);
    }

    public int getId() { return id; }

    public Long getTimeStamp() { return startTimeStamp; }

    public Integer getMagicNumber() { return magicNumber; }

    public String getHash() { return hash; }

    public String getLastHash() { return lastHash; }

    public Integer getDifficulty() { return difficulty; }

    public long getCreationTime() { return creationTime; }
}

class BlockChain {
    private Block lastBlock;
    private static BlockChain blockchainInstance;
    private Integer difficulty;
    private String difficultyCheck;
    private String blockChainName;
    private boolean isValid;
    private final  static long serialVersionUID = 9871236498172387L;

    private BlockChain() {
        blockChainName = "Block.chain";
        difficulty = 0;
        difficultyCheck = getDifficultyCheckString(difficulty);
        if(new File(blockChainName).exists()) {
            lastBlock = checkBlockChain();
            this.isValid = lastBlock != null;
        }else {
            lastBlock = new Block("", 0, 0, "0", "0", new Date().getTime(), 0,serialVersionUID);
        }
    }
    private String getDifficultyCheckString(int difficulty) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < difficulty; i++)
            sb.append("0");
        return sb.toString();
    }
    private Block checkBlockChain() {
        Block block = null;
        try (FileInputStream fis = new FileInputStream(blockChainName)) {
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            do {
                block = (Block) ois.readObject();
                String hash = StringUtil.applySha256(block);
                if (!hash.equals(block.getHash())) {
                    ois.close();
                    System.out.println("Blockchain fail: there are some incorrect blocks..");
                    return null;
                }
            } while (ois.read() == 1); // 1 means another one block-object to read
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(block != null) {
            difficulty = block.getDifficulty();
            difficultyCheck = getDifficultyCheckString(difficulty);
        }
        return block;

    }

    public boolean engageNewBlock(Block offeredBlock) {
        if(lastBlock.getId()+1 == offeredBlock.getId()) {
            String hash = StringUtil.applySha256(offeredBlock);
            if (hash.equals(offeredBlock.getHash())) {
                return saveBlockchainState(offeredBlock);
            }
        }
        return false;
    }
    private synchronized boolean saveBlockchainState(Block block) {
        AppendableSerialization as = new AppendableSerialization(blockChainName, block);
        try {
            as.appendableObjectWriting();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        lastBlock = block;
        difficulty = lastBlock.getDifficulty();
        difficultyCheck = getDifficultyCheckString(difficulty);
        return true;
    }
    /**
     *
     * @param number - points on how much Blocks to show
     */
    public void getBlockchainState(int number) {
        int i = 0;
        if(new File(blockChainName).exists()) {
            try (FileInputStream fis = new FileInputStream(blockChainName)) {
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);

                do {
                    System.out.println((Block) ois.readObject());
                    i++;
                } while (ois.read() == 1 && i < number);
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{System.out.println("There is no file "+blockChainName+" of blockchain");}
    }
    public static BlockChain getInstance() {
        if(blockchainInstance == null)
            blockchainInstance = new BlockChain();
        return blockchainInstance;
    }
    public Block getLastBlock() {
        return lastBlock;
    }
    public String getDifficultyCheck() {
        return difficultyCheck;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getDifficulty() {
        return difficulty;
    }
}

class Miner implements Runnable {
    private final String minerId;
    private BlockChain bc;
    private Block lastBlock;
    private Integer difficulty;
    private String difficultyCheck;

    private Integer newBlockId;
    private String lastHash;
    private String newHash;

    public Miner(BlockChain bc, String minerId) {
        this.minerId = minerId;
        this.bc = bc;
        getConditions();
    }
    private void getConditions() {
        if(bc == null) System.out.println("Block Chain is NULL");
        this.lastBlock = bc.getLastBlock();
        if(lastBlock == null) System.out.println("lastBlock is NULL");
        this.difficulty = bc.getDifficulty();
        this.difficultyCheck = bc.getDifficultyCheck();
        this.newBlockId = lastBlock.getId()+1;
        this.lastHash = lastBlock.getHash();
    }
    @Override
    public void run(){
//        while(true) {
        for(int i = 0; i<5; i++) {
            bc.engageNewBlock(mintBlock());
            getConditions();
        }
    }

    private Block mintBlock() {
        long startTimeStamp = new Date().getTime();
        Integer magicNumber = null;
        int i = 0;
        while(true) {
            magicNumber = new Random().nextInt();
            newHash = StringUtil.applySha256(new StringBuilder()
                    .append(newBlockId)
                    .append(magicNumber)
                    .append(lastHash)
                    .append(startTimeStamp)
                    .toString());
            if(newHash.substring(0, difficulty).equals(difficultyCheck))
                break;
        }
        Block newBlock = new Block(minerId, newBlockId,magicNumber,lastHash,
                newHash, startTimeStamp, difficulty, bc.getSerialVersionUID());
        // difficulty used to create this block
        return newBlock;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        BlockChain bc = BlockChain.getInstance();
        Thread th1 = new Thread(new Miner(bc, "#1"));
        Thread th2 = new Thread(new Miner(bc, "#2"));
        Thread th3 = new Thread(new Miner(bc, "#3"));

        th1.start();
        th1.join();
        th2.start();
        th2.join();
        th3.start();
        th3.join();
        bc.getBlockchainState(5);
    }
}
