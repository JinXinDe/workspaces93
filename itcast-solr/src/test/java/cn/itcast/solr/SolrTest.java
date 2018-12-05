package cn.itcast.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    //新增修改
    @Test
    public void addOrUpdate() {
        TbItem item = new TbItem();
        item.setId(1436707L);
        item.setTitle("222 JBL GO 音乐金砖 蓝牙音箱 低音炮 户外便携音响 迷你小音箱 可免提通话 魂动红");
        item.setPrice(new BigDecimal(219));
        item.setImage("https://item.jd.com/1436707.html");
        item.setCategory("音响");
        item.setStatus("1");

        //保存
        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();
    }

    /**
     * 根据id删除
     */
    @Test
    public void deleteById() {
        solrTemplate.deleteById("1436707");

        //提交
        solrTemplate.commit();
    }

    /**
     * 根据条件删除
     */
    @Test
    public void deleteQuery() {
        //创建查询对象
        SimpleQuery query = new SimpleQuery();

        //设置查询条件 contains不会分词
        Criteria criteria = new Criteria("item_title").contains("222");
        query.addCriteria(criteria);

        solrTemplate.delete(query);

        //提交
        solrTemplate.commit();
    }

    /**
     * 根据分页信息条件查询
     */
    @Test
    public void queryForPage() {
        //创建查询对象query
        SimpleQuery query = new SimpleQuery("item_title:jbl");

        //设置分页
        int page = 1;

        //分页起始索引号，相当于mysql limit 起始索引号,页大小
        //起始索引号 = （页号-1）*页大小
        query.setOffset(0);

        //页大小；默认为10
        query.setRows(10);

        /*
        参数1：查询对象
        参数2：查询返回结果对应的实体类（需要使用@Field）
         */
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    /**
     * 根据多条件查询
     */
    @Test
    public void multiQuery() {
        //创建查询对象query
        SimpleQuery query = new SimpleQuery();

        //设置查询条件 contains不会分词；多条件之间是并列的关系
        Criteria criteria = new Criteria("item_title").contains("jbl");
        query.addCriteria(criteria);

        Criteria criteria1 = new Criteria("item_price").greaterThanEqual(100);
        query.addCriteria(criteria1);

        /*
        参数1：查询对象
        参数2：查询返回结果对应的实体类（需要使用@Field）
         */
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    /**
     * 输出信息
     * @param scoredPage 分页结果对象
     */
    public void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数: " + scoredPage.getTotalElements());
        System.out.println("总页数: " + scoredPage.getTotalPages());

        List<TbItem> itemList = scoredPage.getContent();
        if (itemList != null && itemList.size() > 0) {
            for (TbItem tbItem : itemList) {
                System.out.println("id = " + tbItem.getId());
                System.out.println("title = " + tbItem.getTitle());
                System.out.println("price = " + tbItem.getPrice());
                System.out.println("image = " + tbItem.getImage());
                System.out.println("category = " + tbItem.getCategory());
                System.out.println("status = " + tbItem.getStatus());
            }
        }
    }
}
