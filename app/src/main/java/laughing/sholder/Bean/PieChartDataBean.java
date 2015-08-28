package laughing.sholder.Bean;

import java.util.Iterator;
import java.util.Map;

public class PieChartDataBean {
	
	private float sum = 0.0f;
	
	private Map<String, Float> map;

	public void setMap(Map<String, Float> map ){
		this.map = map;
		Iterator<Map.Entry<String, Float>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Float> entry = iter.next();
			sum += entry.getValue();
		}
	}
	
	public float getSum(){
		return sum;
	}
	
	public Map<String, Float> getMap(){
		return this.map;
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
}
