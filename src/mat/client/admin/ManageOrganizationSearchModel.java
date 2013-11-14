package mat.client.admin;

import java.util.List;
import mat.client.shared.search.SearchResults;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class ManageUsersSearchModel.
 */
public class ManageOrganizationSearchModel implements SearchResults<ManageOrganizationSearchModel.Result>,
IsSerializable {
	
	/**
	 * The Class Result.
	 */
	public static class Result implements IsSerializable {
		
		private String oid;
		/** The org name. */
		private String orgName;
		
		public String getKey() {
			// TODO Auto-generated method stub
			return null;
		}
		/**
		 * @return the oid
		 */
		public String getOid() {
			return oid;
		}
		/**
		 * Gets the org name.
		 * 
		 * @return the org name
		 */
		public String getOrgName() {
			return orgName;
		}
		/**
		 * @param oid the oid to set
		 */
		public void setOid(String oid) {
			this.oid = oid;
		}
		/**
		 * Sets the org name.
		 * 
		 * @param orgName
		 *            the new org name
		 */
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
	}
	
	/** The headers. */
	private static String[] headers = new String[] { "Organization Name", "Organization Id" };
	
	/** The widths. */
	private static String[] widths = new String[] {"50%", "50%"};
	
	/** The data. */
	private List<Result> data;
	
	/** The results total. */
	private int resultsTotal;
	
	/** The start index. */
	private int startIndex;
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#get(int)
	 */
	@Override
	public Result get(int row) {
		return data.get(row);
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getColumnHeader(int)
	 */
	@Override
	public String getColumnHeader(int columnIndex) {
		return headers[columnIndex];
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getColumnWidth(int)
	 */
	@Override
	public String getColumnWidth(int columnIndex) {
		return widths[columnIndex];
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getKey(int)
	 */
	@Override
	public String getKey(int row) {
		return data.get(row).getKey();
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getNumberOfColumns()
	 */
	@Override
	public int getNumberOfColumns() {
		return 2;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getNumberOfRows()
	 */
	@Override
	public int getNumberOfRows() {
		return data != null ? data.size() : 0;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getResultsTotal()
	 */
	@Override
	public int getResultsTotal() {
		return resultsTotal;
	}
	
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getStartIndex()
	 */
	@Override
	public int getStartIndex() {
		return startIndex;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#getValue(int, int)
	 */
	@Override
	public Widget getValue(int row, int column) {
		Label value;
		switch(column) {
			case 0:
				value = new Label(data.get(row).getOrgName());
				break;
			case 1:
				value = new Label(data.get(row).getOid());
				break;
			default:
				value = new Label("Unknown Column Index");
		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#isColumnFiresSelection(int)
	 */
	@Override
	public boolean isColumnFiresSelection(int columnIndex) {
		return columnIndex == 0;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#isColumnSelectAll(int)
	 */
	@Override
	public boolean isColumnSelectAll(int columnIndex) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see mat.client.shared.search.SearchResults#isColumnSortable(int)
	 */
	@Override
	public boolean isColumnSortable(int columnIndex) {
		return false;
	}
	
	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the new data
	 */
	public void setData(List<Result> data) {
		this.data = data;
	}
	
	/**
	 * Sets the results total.
	 * 
	 * @param resultsTotal
	 *            the new results total
	 */
	public void setResultsTotal(int resultsTotal) {
		this.resultsTotal = resultsTotal;
	}
	
	/**
	 * Sets the start index.
	 * 
	 * @param startIndex
	 *            the new start index
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
}
