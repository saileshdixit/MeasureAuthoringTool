package mat.dao.clause;

import java.util.List;

import mat.dao.IDAO;
import mat.model.User;
import mat.model.clause.CQLLibrary;

public interface CQLLibraryDAO extends IDAO<CQLLibrary, String>{
	
		public List<CQLLibrary> search(String searchText, String searchFrom, int pageSize, User user, int filter);

		public boolean isLibraryLocked(String id);

		public void updateLockedOutDate(CQLLibrary existingLibrary);

		public String findMaxVersion(String setId);

		public String findMaxOfMinVersion(String setId, String version);

		List<CQLLibrary> searchForIncludes(String searchText);
}
