根据简历数据计算几个报告内容
分为几个大板块：

resume  简历计算
1、人才地域\职能分布 type=city_position_count
2、人才地域流动 type=flow
3、人才职能流动 type=func_flow
position 岗位计算
4、职位分布 type=city_position_count_demand
exponention 指数计算
5、职能供需指数 type=exponention_func
6、岗位供需指数 type=exponention_position
7、岗位关键词 type=position_keyword
8、jd岗位关键词 type=jd_position_keyword
9、简历城市分布 type=resume_city_distribution
//人才分布也从resume_flow 中聚合
10、简历人才流动 type = resume_flow