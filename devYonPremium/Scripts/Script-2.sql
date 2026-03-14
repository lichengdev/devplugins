select
   so_saleorder_b. nastnum as 销售订单数量,
 ic_saleout_b.nassistnum as 出库单实发数量  , 
 ic_saleout_b.vsourcebillcode  
from
  so_saleorder_b
  LEFT JOIN ic_saleout_b on ic_saleout_b.csourcebillbid = so_saleorder_b.csaleorderbid
  INNER  JOIN so_saleorder on ic_saleout_b.csaleorderid = so_saleorder.csaleorderid
  
where
  ic_saleout_b.dr = 0
  and so_saleorder_b.dr = 0
  and ic_saleout_b.vsourcebillcode ='SO302023100901978011'
  and so_saleorder_b.pk_org = (select pk_org from org_orgs where code='01010101')
  and so_saleorder.ctrantypeid IN (select   pk_billtypeid  from bd_billtype where  pk_billtypecode in('30-Cxx-020','30-Cxx-021'))
 
  order by  ic_saleout_b.vsourcebillcode 