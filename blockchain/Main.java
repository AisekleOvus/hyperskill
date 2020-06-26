
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

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
}

class Block {
    private final Integer id;
    private final Long timeStamp = new Date().getTime();
    private final String lastHash;
    private final String hash;


    public Block(Integer id, String lastHash) {
        this.lastHash = lastHash;
        this.id = id;
        this.hash = StringUtil.applySha256(new StringBuilder().append(this.hashCode()).append(id).append(timeStamp).append(lastHash).toString());
    }

    public Block createNewBlock(){
        return new Block(id+1, hash);
    }

    public String getHash() { return hash;}
    public Long getTimeStamp() { return timeStamp;}
    public Integer getId() { return id;}

    @Override
    public String toString() {
        return String.format("Block:\nId: %d\nTimestamp: %d\nHash of the previous block:\n%s\nHash of the block:\n%s\n\n",
                id, timeStamp,lastHash,hash);
    }
}

class BlockChain {
    LinkedList<Block> blockchain = new LinkedList<>();
    public BlockChain() {
        blockchain.add(new Block(0,"0"));
    }
    public void mintOneBlock() {
        blockchain.add(blockchain.getLast().createNewBlock());
    }
    public boolean validate() {
        String lastHash = null;
        String hash = null;
        for(Block block : blockchain) {
            if(lastHash != null) {
                hash = StringUtil.applySha256(new StringBuilder().append(block.hashCode()).append(block.getId())
                        .append(block.getTimeStamp()).append(lastHash).toString());
                if(!block.getHash().equals(hash))
                    return false;
            }
            lastHash = block.getHash();
        }
    return true;
    }
    public void state() {
        blockchain.forEach(System.out::print);
    }
}

public class Main {
    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        for(int i = 0; i < 10; i++) {
            bc.mintOneBlock();
        }
        bc.state();
        System.out.println(bc.validate());
    }
}
