package stellar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//create a completely new and unique pair of keys.
//see more about KeyPair objects: https://stellar.github.io/java-stellar-sdk/org/stellar/sdk/KeyPair.html
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Operation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.TransactionsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.TransactionDeserializer;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.xdr.Int64;

import shadow.okhttp3.HttpUrl;

//The SDK does not have tools for creating test accounts, so you'll have to
//make your own HTTP request.
import java.net.*;
import java.io.*;
import java.util.*;


public class Test 
{

	public static void main(String[] args) throws MalformedURLException, IOException 
	{
		// TODO Auto-generated method stub
//		KeyPair pair = KeyPair.random();
//		System.out.println(new String(pair.getSecretSeed()));
//		//SAV76USXIJOBMEQXPANUOQM6F5LIOTLPDIDVRJBFFE2MDJXG24TAPUU7
//		System.out.println(pair.getAccountId());
		//GCFXHS4GXL6BVUCXBWXGTITROWLVYXQKQLF4YH5O5JT3YZXCYPAFBJZB
		
//		String publicKey = "GDDPY2U7I6EMKXL6PHDJGN6MH5FP4YPEGNMVPLWPIEGETUC6SJQ62CCC";
//		KeyPair pair = KeyPair.fromAccountId(publicKey);
		
		
		
		
		
//		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", pair.getAccountId());
//		InputStream response = new URL(friendbotUrl).openStream();
//		String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
//		System.out.println("SUCCESS! You have a new account :)\n" + body);
//		
//		
		
		
		String accountID= "GALSKIYKCOUFXMCL4J3EOLZESXQAOXNHKQSOWVFWHUYCH5ERZNYMYBR3";
		String uri_str = "https://horizon-testnet.stellar.org/accounts/" + accountID + "/transactions";
		URL url = new URL(uri_str);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
		conn.setRequestMethod("GET");
		conn.connect();
		int responsecode = conn.getResponseCode(); 
		
		String inline="";
		if(responsecode != 200)
			throw new RuntimeException("HttpResponseCode: " + responsecode);
		else
		{
			Scanner sc = new Scanner(url.openStream());
			while(sc.hasNext()){
				inline+=sc.nextLine();
			}
			sc.close();
		}
		
		JSONParser parse = new JSONParser();
		try {
			JSONObject jobj = (JSONObject)parse.parse(inline);
			JSONObject embedded = (JSONObject) jobj.get("_embedded");
			JSONArray records = (JSONArray) embedded.get("records");
			Iterator<?> i = records.iterator();
			
			int counter = 0;
			while (i.hasNext()) 
			{
				JSONObject record = (JSONObject) i.next();
				String id, source, time, envelop_xdr, memo;
				String dest = "";
				int fee;
				long amount;
				
				boolean status = (Boolean)record.get("successful");
				id = (String)record.get("id");
				source = (String)record.get("source_account");
				time = (String)record.get("created_at");
				envelop_xdr = (String)record.get("envelope_xdr");				
				memo = (String)record.get("memo");
				
				if(counter != 0) 
				{	
					// Envelop + dest
					Transaction t = Transaction.fromEnvelopeXdr(envelop_xdr);
					Operation[] op = t.getOperations();
					org.stellar.sdk.xdr.Operation pop = op[0].toXdr();	
					KeyPair p = KeyPair.fromXdrPublicKey(pop.getBody().getPaymentOp().getDestination().getAccountID());
					
					dest = p.getAccountId();
					amount = pop.getBody().getPaymentOp().getAmount().getInt64();
					fee = t.getFee();
					
					System.out.println(id+"\t"+status+"\t"+source+"\t"+dest+"\t"+time+"\t"+"\t"+memo+"\t"+amount/10000000);
				}
				else {
					System.out.println("Create Account From Friendbot");
				}
				counter++;
				
				
		    }
						
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

//		KeyPair pair = KeyPair.random();
//		String sSeed = new String(pair.getSecretSeed());
//		String accountID = pair.getAccountId();
//		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", pair.getAccountId());
//		InputStream response = null;
//		try {
//			response = new URL(friendbotUrl).openStream();
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
//		System.out.println("SUCCESS! You have a new account :)\n" + body);
		
	}

}





