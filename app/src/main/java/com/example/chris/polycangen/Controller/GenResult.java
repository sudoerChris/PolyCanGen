package com.example.chris.polycangen.Controller;
import android.os.AsyncTask;
import com.example.chris.polycangen.Model.ItemNode;
import java.security.SecureRandom;
import java.util.Locale;
public class GenResult extends AsyncTask<Void,ItemNode,ItemNode> {
	public interface GenResultIF {
		 void GRUpdate(ItemNode node, String hint);
		 void GRDone(ItemNode node, String hint);
	}
	private ItemNode[] itemList;
	private int weightTotal = 0;
	private GenResultIF target;
	public GenResult(ItemNode[] list, GenResultIF target){
		this.itemList = list;
		for (ItemNode node:list) {
				weightTotal+=node.Weight;
		}
		this.target = target;
	}
	@Override
	protected ItemNode doInBackground(Void... params) {
		ItemNode result = null;
		SecureRandom rand = new SecureRandom();
		int randResult = rand.nextInt(weightTotal);
		for(ItemNode node: itemList){
			randResult-=node.Weight;
			if(randResult<=0){
				result = node;
				break;
			}
		}
		if(itemList.length<=1){
			return result;
		}
		for(double i = 4;i<=6;i+=0.15){
			if(isCancelled()){
				return result;
			}
			publishProgress(itemList[rand.nextInt(itemList.length)]);
			try {
				Thread.sleep(((Double)Math.exp(i)).intValue());
			}catch (InterruptedException IEx){
				return result;
			}
		}
		return result;
	}
	
	@Override
	protected void onProgressUpdate(ItemNode... values) {
		super.onProgressUpdate(values);
		if(target!=null){
			target.GRUpdate(values[0],"");
		}
	}
	
	@Override
	protected void onPostExecute(ItemNode o) {
		super.onPostExecute(o);
		
		if(target!=null&&o!=null){
			target.GRDone(o,String.format(Locale.US,"%.2f%% Probability",100.0*o.Weight/weightTotal));
		}
	}
}
