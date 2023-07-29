package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * JdbcTemplate 사용
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    /**
     * 회원 가입
     */
    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id,money) values (?,?)";
        template.update(sql,member.getMemberId(),member.getMoney());
        return member;
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
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs,rowNum)->{
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
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
        template.update(sql,money,memberId);
    }


    /**
     * 회원정보 삭제
     * @param memberId
     * @throws SQLException
     */
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";
        template.update(sql,memberId);
    }

}
