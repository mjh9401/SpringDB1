package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQlExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    /**
     * 회원 가입
     */
    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id,money) values (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw exTranslator.translate("save",sql,e);
        } finally {
           close(con,pstmt,null);
        }

    }

    /**
     * 회원 조회
     * @param memberId
     * @return 조회 결과
     * @throws SQLException
     */
    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";
        Connection con= null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId="+memberId);
            }

        } catch (SQLException e) {
           throw exTranslator.translate("save",sql,e);
        } finally {
            close(con,pstmt,rs);
        }
    }

    /**
     * 회원 정보 수정
     * @param memberId
     * @param money
     * @throws SQLException
     */
    @Override
    public void update(String memberId, int money){
        String sql = "update member set money=? where member_id = ?";

        Connection con =null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("save",sql,e);
        }finally {
            close(con,pstmt,null);
        }
    }


    /**
     * 회원정보 삭제
     * @param memberId
     * @throws SQLException
     */
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";

        Connection con =null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("save",sql,e);
        }finally {
            close(con,pstmt,null);
        }
    }


    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con,dataSource);
    }

    private Connection getConnection(){
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con= DataSourceUtils.getConnection(dataSource);
        log.info("get connection ={}, class={}",con,con.getClass());
        return con;
    }
}
