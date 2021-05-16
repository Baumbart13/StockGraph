package at.htlAnich.backTestingSuite;

import at.htlAnich.tools.database.CanBeTable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Depot implements CanBeTable {
	protected List<DepotPoint> mPoints = null;

	public Depot(DepotPoint ... points){
		var sortedTemp = Arrays.stream(points).toList();
		Collections.sort(sortedTemp);
		this.mPoints.addAll(mPoints);
	}

	public Depot(){
		mPoints = new LinkedList<>();
	}

	public List<DepotPoint> getAll(LocalDate date){
		List<DepotPoint> out = new LinkedList<>();
		for(var point : mPoints){
			if(point.getDate().equals(date)){
				out.add(point);
			}
		}
		return out;
	}

	public List<DepotPoint> getAll(String symbol){
		List<DepotPoint> out = new LinkedList<>();
		for(var point : mPoints){
			if(point.getSymbol().equals(symbol)){
				out.add(point);
			}
		}
		return out;
	}

	public DepotPoint getPoint(String symbol, LocalDate date){
		for(var point : mPoints){
			if(point.getSymbol().equals(symbol) && point.getDate().equals(date)){
				return point;
			}
		}
		return new DepotPoint();
	}

	public DepotPoint getPoint(int i){
		if(i >= mPoints.size()){
			return new DepotPoint();
		}
		return mPoints.get(i);
	}

	public int getIndexOfPoint(String symbol, LocalDate date){
		for(int i = 0; i < mPoints.size(); ++i){
			if(mPoints.get(i).getSymbol().equals(symbol) && mPoints.get(i).getDate().equals(date)){
				return i;
			}
		}
		return -1;
	}

	public int getIndexOfPoint(DepotPoint point){
		return getIndexOfPoint(point.getSymbol(), point.getDate());
	}

	public void addDepotPoint(DepotPoint point){
		int index = 0;
		for( ; index < mPoints.size(); ++index){
			if(point.getDate().isBefore(mPoints.get(index).getDate())){
				break;
			}
		}
		mPoints.add(index, point);
	}

	public void removeDepotPoint(DepotPoint point){
		mPoints.remove(point);
	}

	public void removeDepotPoint(int index){
		mPoints.remove(index);
	}

	@Override
	public String getTableName() {
		return BacktestingDatabase._TABLE_NAME;
	}
}
