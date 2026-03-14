
-- SELECT code,name,itemkey  FROM wa_item  where code='f_1'
-- SELECT code,name,itemkey  FROM wa_item  where name='应发合计供电' 应发合计
 
--SELECT * FROM wa_data WHERE c_7 ='060015'

SELECT sum(f_308)AS 应发合计供电,sum(f_1)AS 应发合计,sum(f_2),sum(f_3) ,sum(f_4),sum(f_5),sum(f_6) ,sum(f_7),sum(f_8),sum(f_9) fROM (
SELECT * fROM (
 SELECT
 	psndoc. name AS 员工 ,
	wa_data.f_308,/*供电使用*/
	wa_data.f_1 AS 应发合计 ,
	wa_data.f_1 ,
	wa_data.f_2,
	wa_data.f_3,
	wa_data.f_4,
	wa_data.f_5, 
	wa_data.f_6,
	wa_data.f_7, 
	wa_data.f_8,
	wa_data.f_9, 
	wa_data.cyear AS cyear,
	wa_data.cperiod AS MONTH,
	(wa_data.cyear || '-' || wa_data.cperiod) AS cperiod,
	org.code AS orgcode,
	org.name AS orgname,
	wa_data.c_7 AS workdept,
	dept.code AS deptcode,
	dept.name AS deptname,
	waclass.code AS classcode,
	waclass.name AS classname,
	wa_data.pk_wa_data,
	org.pk_org,
	dept.pk_dept,
	waclass.pk_wa_class
 
FROM
	wa_data wa_data
	INNER  JOIN hi_psnjob  psnjob ON wa_data.pk_psnjob = psnjob.pk_psnjob 
	AND wa_data.pk_psndoc = psnjob.pk_psndoc 
	/* AND psnjob.LASTFLAG ='Y' AND psnjob.ismainjob ='Y' AND psnjob.ENDFLAG ='N' */
	INNER  JOIN bd_psndoc  psndoc ON wa_data.pk_psndoc  = psndoc.pk_psndoc  
	LEFT  JOIN wa_waclass  waclass ON wa_data.pk_wa_class = waclass.pk_wa_class 
	LEFT  JOIN org_orgs  org ON wa_data.pk_org = org.pk_org 
 	LEFT  JOIN org_dept  dept ON wa_data.workdept = dept.pk_dept 
	/* LEFT  JOIN org_dept  dept ON psnjob.pk_dept = dept.pk_dept */
			
WHERE
	wa_data.checkflag = 'Y' /* 审核标志 */
	AND wa_data.checkflag = 'Y' /* 计算标志 */
	AND wa_data.stopflag = 'N' /* 停发标志  */
	--AND wa_data.pk_wa_class = '1001A110000000005KOL'
)
WHERE  	
          cperiod  >= '2023-01' 
	  AND cperiod <=  '2023-02' 
    AND orgname ='北京市地铁运营有限公司机电分公司' AND  workdept='技术部'   AND classname='工资表'
--	 AND  orgname ='北京市地铁运营有限公司供电分公司'  AND deptcode='GD029'  AND classname IN('月度工资表','年终奖发放')
--     AND  orgname ='北京市地铁运营有限公司线路分公司'  AND pk_dept='1001111000000008GCL3'  AND pk_wa_class IN('1001111000000000HC6I')
--     AND  orgname ='北京市地铁运营有限公司通信信号分公司'  AND pk_dept='100111100000000E29BR'  AND pk_wa_class IN('1001111000000000DJ21')
  --  AND  pk_org ='00012210000000000BO9'  AND pk_dept IN ('10012210000000003740','10012210000000003762')  AND pk_wa_class IN('1001111000000000GZ5M')--机关 机关部室技术部
--   AND  orgname ='北京市地铁运营有限公司运营一分公司'  AND deptname='机关部室技术部'  AND classname IN('月度工资')
 --  AND    orgname ='北京市地铁运营有限公司运营二分公司'   AND deptcode='YY57'   AND   pk_wa_class IN('1001111000000005HJ1G'，'1001111000000000JYYD')
--  	AND  orgname ='北京市地铁运营有限公司运营三分公司'  AND deptname='技术部'  AND classname IN('年终奖','月薪') 
--   	AND  orgcode ='08'  AND deptcode='YS110116'  AND classcode IN('01')
	 
	 ORDER BY  cperiod
   )
