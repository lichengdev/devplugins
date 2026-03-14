SELECT
	ht.pk_group,
	--合同集团
 ht.pk_org,
	--合同组织
 ht.vdef11,
	--合同号
 ht.pk_zl_ht,
	substr(cf.endtime, 0, 7) kjqj,
	--会计期间
 kh.code khcode,
	--客户编码
 kh.name khname,
	--客户名称
 xm.xmcode,
	--项目编码
 xm.xmname,
	--项目名称
 htzje.yhhhtzje conamount,
	substr(ht1.ksrq, 0, 11)|| '-' || substr(ht2.jsrq, 0, 11) zlrq,
	--租赁起止日期
 ht.nhtarea htmj,
	--合同面积
 sfbz.name sfbz,
	--付款方式
 fymx.nprice dj,
	---单价
 substr(ht.yhksrq, 0, 11)|| '-' || substr(ht.yhjsrq, 0, 11) mzq,
	--免租期
 ysjsd.jsje ysjsje,
	--预收结算金额
 ysjsd.wsje yswsje,
	--预收无税金额
 ysjsd.se ysse,
	---预收税额
 yisjsd.jsje yisjsje,
	--应收结算金额
 yisjsd.wsje yiswsje,
	--应收无税金额
 yisjsd.se yisse,
	--应收税额
 NULL AS T,
	NULL AS U,
	NULL AS V,
	jsd3.jsje j3jsje,
	jsd3.wsje j3wsje,
	jsd3.se j3se,
	jsd4.jsje j4jsje,
	jsd4.wsje j4wsje,
	jsd4.se j4se,
	NULL AS xj,
	fyqd.ysje fyysje,
	fyqd.wsje fywsje,
	fyqd.se fyse,
	jsd5.jzrq,
	ht5.ljskje,
	NULL AS z20,
	NULL AS z21,
	fyqd1.ysje fy1ysje,
	fyqd1.wsje fy1wsje,
	fyqd1.se fy1se,
	ht6.ljskje ht2ljskje,
	ht6.ljskwsje ht2ljskwsje,
	ht6.ljskse ht2ljskse
FROM
	fdcproc_zl_ht ht
	--inner join fdcproc_zl_ht_fc fc on ht.pk_zl_ht=fc.pk_zl_ht
  inner join fdcproc_zl_ht_zjcf cf on cf.pk_zl_ht=ht.pk_zl_ht
  --inner join fdcpr_bd_project xm on fc.vbdef6=xm.pk_project
  inner join bd_customer kh on ht.vdef9=kh.pk_customer
  inner join (select distinct min(dstartdate) ksrq,vdef11 from fdcproc_zl_ht ht1 where nvl(dr,0)=0 and  ht1.ihtstatus=1 and fstatusflag='1'  and ht1.cbilltypecode='H315'group by vdef11) ht1 on  ht1.vdef11=ht.vdef11
  inner join (select distinct max(denddate) jsrq,vdef11 from fdcproc_zl_ht ht2 where nvl(dr,0)=0 and ht2.ihtstatus=1  and fstatusflag='1'  and ht2.cbilltypecode='H315' group by vdef11) ht2 on ht2.vdef11=ht.vdef11
  inner join (select distinct pk_zl_ht,pk_chargestand,nprice from fdcproc_zl_ht_fyxm where nvl(dr,0)=0 and crowno='10') fymx on ht.pk_zl_ht=fymx.pk_zl_ht
  inner join (select sum(yhhhtzje) yhhhtzje ,vdef11 from fdcproc_zl_ht where nvl(dr,0)=0 and cbilltypecode='H315' and fstatusflag='1' group by vdef11) htzje on htzje.vdef11=ht.vdef11
  inner join fdcprcm_bd_std sfbz on fymx.pk_chargestand=sfbz.pk_charge_std  
  inner join (select distinct xm.code xmcode,xm.name xmname,fc.pk_zl_ht from fdcpr_bd_project xm inner join fdcproc_zl_ht_fc fc on fc.vbdef6=xm.pk_project where nvl(xm.dr,0)=0 and nvl(fc.dr,0)=0 ) xm on xm.pk_zl_ht=ht.pk_zl_ht
 left join ( select sum(a.norigbrevmny)+sum(nvl(hc.norigbrevmny,0)) jsje,sum(a.norignotaxmny)+sum(nvl(hc.norignotaxmny,0)) wsje,sum(a.norigbtaxmny)+sum(nvl(hc.norigbtaxmny,0)) se,a.dstartdate,b.dbilldate,a.csrcbid  
            from fdcpr_hx_billdetail a inner join fdcpr_hx_busitrans b on a.pk_busitrans=b.pk_busitrans 
            left join   fdcpr_hx_billdetail hc on a.pk_billdetail = hc.vbdef10 where  nvl(a.dr,0)=0          
            group by a.dstartdate,a.csrcbid,b.dbilldate ) ysjsd on ysjsd.csrcbid=cf.pk_zl_ht_zjcf and substr(cf.endtime,0,7)>substr(ysjsd.dbilldate,0,7) and substr(cf.endtime,0,7)=substr(ysjsd.dstartdate,0,7)
  left join ( select sum(a.norigbrevmny) jsje,sum(a.norignotaxmny) wsje,sum(a.norigbtaxmny) se,a.dstartdate,b.dbilldate,a.csrcbid  
            from fdcpr_hx_billdetail a inner join fdcpr_hx_busitrans b on a.pk_busitrans=b.pk_busitrans where  nvl(a.dr,0)=0  
            group by a.dstartdate,a.csrcbid,b.dbilldate ) yisjsd on yisjsd.csrcbid=cf.pk_zl_ht_zjcf and substr(cf.endtime,0,7)=substr(yisjsd.dbilldate,0,7) and substr(cf.endtime,0,7)=substr(yisjsd.dstartdate,0,7)
  left join ( select sum(a.norigbrevmny) jsje,sum(a.norignotaxmny) wsje,sum(a.norigbtaxmny) se,a.dstartdate,b.dbilldate,a.csrcbid  
            from fdcpr_hx_billdetail a inner join fdcpr_hx_busitrans b on a.pk_busitrans=b.pk_busitrans where  nvl(a.dr,0)=0  
            group by a.dstartdate,a.csrcbid,b.dbilldate ) jsd3 on jsd3.csrcbid=cf.pk_zl_ht_zjcf and substr(cf.endtime,0,7)=substr(jsd3.dbilldate,0,7) and substr(cf.endtime,0,7)>substr(jsd3.dstartdate,0,7)
  left join ( select sum(a.norigbrevmny) jsje,sum(a.norignotaxmny) wsje,sum(a.norigbtaxmny) se,b.dbilldate,a.csrcid  
            from fdcpr_hx_billdetail a inner join fdcpr_hx_busitrans b on a.pk_busitrans=b.pk_busitrans where  nvl(a.dr,0)=0  and substr(b.dbilldate,0,7)<substr(a.dstartdate,0,7)
            group by a.csrcid,b.dbilldate ) jsd4 on jsd4.csrcid=ht.pk_zl_ht and substr(cf.endtime,0,7)=substr(jsd4.dbilldate,0,7)          
 left join (select  vdef2 srcbid,sum(norigchargemny) ysje,sum(norigcorpusmny) wsje,sum(norigtaxexpenmny)se 
           from fdcprcm_cm_fee_list where nvl(dr,0)=0  and vdef3='1001ZZ100000000DRO6K' group by vdef2) fyqd on cf.pk_zl_ht_zjcf=fyqd.srcbid
           
 left join (select substr(max(ddeaddate),0,11) jzrq,csrcid from  fdcpr_hx_billdetail where nvl(dr,0)=0 group by csrcid) jsd5 on ht.pk_zl_ht=jsd5.csrcid
 inner join (select a.vdef11 hth,sum(b.ljskje) ljskje from fdcproc_zl_ht a inner join fdcproc_zl_ht_zjcf b on a.pk_zl_ht=b.pk_zl_ht where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.endtime is not null
 and a.cbilltypecode='H315' and a.fstatusflag='1'
  group by a.vdef11 ) ht5 on ht5.hth=ht.vdef11 
 
 left join (select  a.pk_contract srcid,sum(a.norigchargemny)+sum(nvl(b.norigchargemny,0)) ysje,sum(a.norigcorpusmny)+sum(nvl(b.norigcorpusmny,0)) wsje,sum(a.norigtaxexpenmny)+sum(nvl(b.norigtaxexpenmny,0)) se 
           from fdcprcm_cm_fee_list a left join fdcprcm_cm_fee_list b on a.pk_fee=b.csrcid
           where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.vdef3='1001ZZ100000000DRO6K' group by a.pk_contract) fyqd1 on ht.pk_zl_ht=fyqd1.srcid 
           
 left join (select a.pk_zl_ht,sum(b.ljskje) ljskje,sum(b.ljskwsje) ljskwsje,sum(b.ljskse) ljskse  from fdcproc_zl_ht a 
 inner join fdcproc_zl_ht_zjcf b on  a.pk_zl_ht=b.pk_zl_ht where nvl(a.dr,0)=0 and nvl(b.dr,0)=0  and b.sjzt='1001ZZ100000000DRO6H' and b.endtime is not null
 and a.cbilltypecode='H315' and a.fstatusflag='1'
  group by a.pk_zl_ht ) ht6 on ht6.pk_zl_ht=ht.pk_zl_ht 
 
 where  cf.endtime is not null and ht.ihtstatus=1 and ht.fstatusflag='1' and nvl(cf.dr,0)=0 and ht.cbilltypecode='H315' and nvl(ht.dr,0)=0 
 