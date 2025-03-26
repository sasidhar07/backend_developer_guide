package digit.repository.querybuilder;

import digit.web.models.BirthApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class BirthApplicationQueryBuilder {

    private static final String BASE_BTR_QUERY = " SELECT btr.id as bid, btr.tenantid as btenantid, btr.applicationnumber as bapplicationnumber, btr.babyfirstname as bbabyfirstname, btr.babylastname as bbabylastname, btr.fatherid as bfatherid, btr.motherid as bmotherid, btr.doctorname as bdoctorname, btr.hospitalname as bhospitalname, btr.placeofbirth as bplaceofbirth, btr.timeofbirth as btimeofbirth, btr.createdby as bcreatedby, btr.lastmodifiedby as blastmodifiedby, btr.createdtime as bcreatedtime, btr.lastmodifiedtime as blastmodifiedtime, ";

    private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.type as atype, add.address as aaddress, add.city as acity, add.pincode as apincode, add.registrationid as aregistrationid ";

    private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY btr.createdtime DESC ";
    /**
     * Constructs a dynamic SQL query based on the search criteria provided.
     * 
     * @param criteria        The search criteria containing filters for tenant ID, status, IDs, and application number.
     * @param preparedStmtList The list to store values for prepared statements.
     * @return The final SQL query string with appropriate conditions.
     */

    public String getBirthApplicationSearchQuery(BirthApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        //preparedStmList need?
        StringBuilder query = new StringBuilder(BASE_BTR_QUERY);
        query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }
        if(!ObjectUtils.isEmpty(criteria.getStatus())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }
        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }
        if(!ObjectUtils.isEmpty(criteria.getApplicationNumber())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.applicationnumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }

        // order birth registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        return query.toString();
    }
    /**
     * Adds a WHERE or AND clause to the query based on the conditions.
     *
     * @param query           The SQL query being constructed.
     * @param preparedStmtList The list of prepared statement values.
     */

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }
    /**
     * Creates a query placeholder string for multiple IDs in the IN clause.
     *
     * @param ids The list of IDs.
     * @return A comma-separated list of '?' placeholders for prepared statements.
     */
    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }
    /**
     * Adds the list of IDs to the prepared statement parameter list.
     *
     * @param preparedStmtList The list to store values for prepared statements.
     * @param ids The list of IDs to be added.
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}