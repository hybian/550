package Player;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BetSingleton 
{
	static BetSingleton bet = new BetSingleton();
	
	protected String aliceId = "GBEA5F4T3PVII4ZUUECRS3SFOFASY3SMPR7RVSDW6INMAXJQIIVUBLIF";
	protected String aliceSeed = "SDNZQB3O2E42WWXME62YHNG4KW76GOA3WDSLUOH4NJXQV5FDNCCTHDR6";
	
	protected String bobId = "GCIXANWGH5FLBB4M2V3GVJXWX67J7KYMCWRDRGFN7MELFPUI2YAZHUAO";
	protected String bobSeed = "SBHNW2LTFGZHWXW6S7KT3GVIJTD5M5TAFBBBVXIJHUZYJLYRT6SFJT23";
	
	protected String bankerId = "GAGKSXWDHEQNVP4TPIEP6ZGV4MFJC7LTGTUANKN3VYIWQ3ZWSZEXFPCZ";
	protected String bankerSeed = "SDWOQHVCW5XUCRDRBWX6ROTHR2DJH2LMHH2MTSCEZHVCRTZGKLODII5H";
	
	protected boolean isAlicePlaying = false;
	protected boolean isBobPlaying = false;
	
	protected boolean hasNumAlice = false;
	protected boolean hasBetAlice = false;
	protected boolean hasRevAlice = false;
	
	protected boolean hasNumBob = false;
	protected boolean hasBetBob = false;
	protected boolean hasRevBob = false;
	
	protected int AliceNum = -1;
	protected int BobNum = -1;
	
	protected String AliceHash;
	protected String BobHash;
	
	protected String winner;
	
	protected ArrayList<String> history = new ArrayList<>();
	
	
	private BetSingleton() 
	{}
	
	public static BetSingleton getBet() 
	{
		return bet;
	}
	
	
	protected void reset() 
	{
		System.out.println("RESET Called");
		isAlicePlaying = true;
		isBobPlaying = true;
		
		hasNumAlice = false;
		hasBetAlice = false;
		hasRevAlice = false;
		
		hasNumBob = false;
		hasBetBob = false;
		hasRevBob = false;
		
		AliceNum = -1;
		BobNum = -1;
		
		AliceHash = "";
		BobHash = "";
		
		winner = "";
		
		history = new ArrayList<>();
	}	
	
	
	protected String getMD5(String string) // 28 bytes
	{
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md5.update(StandardCharsets.UTF_8.encode(string));
		String md5_str = String.format("%032x", new BigInteger(1, md5.digest()));
		String return_str = md5_str.substring(0, 27);
		return return_str;
	}
	
}
