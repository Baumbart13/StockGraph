package at.htlAnich.backTestingSuite;

import at.htlAnich.tools.database.CanBeTable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A Collection of Depot-Rows. A different depot means a different strategy.
 */
public class Depot implements CanBeTable {
	protected List<DepotPoint> mPoints = null;
	protected Strategy mStrategy = null;

	/**
	 * The strategies that can be used on <code>Depot</code>.
	 */
	public static enum Strategy{
		NONE,
		avg200,
		avg200_3percent,
		buyAndHold,
		avg200_false;
	}

	public Depot(Strategy strat, DepotPoint ... points){
		var sortedTemp = Arrays.asList(points);
		Collections.sort(sortedTemp);
		this.mPoints.addAll(mPoints);
		this.mStrategy = strat;
	}

	/**
	 * Default-constructor just initializes the points and the strategy, to preserve a null-pointer-exception.
	 */
	public Depot(){
		mPoints = new LinkedList<>();
		mStrategy = Strategy.NONE;
	}

	/**
	 * Returns all <code>DepotPoints</code> from a specific date, no matter what stock it is.
	 * @param date The specific date.
	 * @return a collection of <code>DepotPoints</code> of one day.
	 */
	public List<DepotPoint> getAll(LocalDate date){
		List<DepotPoint> out = new LinkedList<>();
		for(var point : mPoints){
			if(point.getDate().equals(date)){
				out.add(point);
			}
		}
		return out;
	}

	/**
	 * Returns all <code>DepotPoints</code> from a specific stock, no matter what date it is.
	 * @param symbol The ticker-name of the stock.
	 * @return a collection of <code>DepotPoints</code> of one stock.
	 * @see at.htlAnich.stockUpdater.api.ApiParser.Function LISTING_STATUS.
	 */
	public List<DepotPoint> getAll(String symbol){
		List<DepotPoint> out = new LinkedList<>();
		for(var point : mPoints){
			if(point.getSymbol().equals(symbol)){
				out.add(point);
			}
		}
		return out;
	}

	/**
	 * Returns a <code>DepotPoint</code> from a specific stock and a specific date.
	 * @param symbol The ticker-name of the stock.
	 * @param date The date of the <code>DepotPoint</code>.
	 * @return if there is an entry, then <code>DepotPoint</code> of given stock and date will be returned, else an empty
	 * <code>DepotPoint</code>-Object.
	 */
	public DepotPoint getPoint(String symbol, LocalDate date){
		for(var point : mPoints){
			if(point.getSymbol().equals(symbol) && point.getDate().equals(date)){
				return point;
			}
		}
		return new DepotPoint();
	}

	/**
	 * Returns the number of elements, that are inside of this <code>Depot</code>-object.
	 * @return a non-negative integer holding the amount of elements inside this object.
	 */
	public int numberOfElements(){
		return this.mPoints.size();
	}

	/**
	 * Returns a <code>DepotPoint</code> at given index.
	 * @param i the index of the element, that will be returned.
	 * @return if <code>i</code> is greater than or equals <code>numberOfElements</code> an empty <code>DepotPoint</code>
	 * will be returned, otherwise the requested one.
	 */
	public DepotPoint getPoint(int i){
		if(i >= mPoints.size()){
			return new DepotPoint();
		}
		return mPoints.get(i);
	}

	/**
	 * Returns the index of the corresponding tickerSymbol and date.
	 * @param symbol the ticker of the stock.
	 * @param date the date of the <code>DepotPoint</code>.
	 * @return a non-negative integer, if there is an element. Otherwise <code>-1</code> will be returned.
	 */
	public int getIndexOfPoint(String symbol, LocalDate date){
		for(int i = 0; i < mPoints.size(); ++i){
			if(mPoints.get(i).getSymbol().equals(symbol) && mPoints.get(i).getDate().equals(date)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the corresponding <code>DepotPoint</code>.
	 * @param point the <code>DepotPoint</code> which index will be looked up.
	 * @return a non-negative integer, if there is an element. Otherwise <code>-1</code> will be returned.
	 */
	public int getIndexOfPoint(DepotPoint point){
		return getIndexOfPoint(point.getSymbol(), point.getDate());
	}

	/**
	 * A <code>DepotPoint</code> will be added to this <code>Depot</code>.
	 * @param point the point, that will be added.
	 */
	public void addDepotPoint(DepotPoint point){
		int index = 0;
		for( ; index < mPoints.size(); ++index){
			if(point.getDate().isBefore(mPoints.get(index).getDate())){
				break;
			}
		}
		mPoints.add(index, point);
	}

	/**
	 * Removes the first entry of given <code>DepotPoint</code>.
	 * @param point the element, that shall be removed.
	 * @return <code>true</code> if the element existed.
	 */
	public boolean removeDepotPoint(DepotPoint point){
		return mPoints.remove(point);
	}

	/**
	 * Removes the entry at given index if
	 * @param index the index of element, that shall be removed.
	 * @return <code>true</code> if the element existed. <code>false</code> if <code>inde</code>
	 */
	public boolean removeDepotPoint(int index){
		if(index >= mPoints.size()) return false;
		var x = mPoints.remove(index);
		return (x != null);
	}

	public DepotPoint popDepotPoint(int index){
		return mPoints.remove(index);
	}

	@Override
	public String getTableName() {
		return BacktestingDatabase._TABLE_NAME;
	}

	public Strategy getStrategy(){
		return this.mStrategy;
	}
}
