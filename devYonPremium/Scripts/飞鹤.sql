select
   so_saleorder_b. nastnum as 销售订单数量,
 ic_saleout_b.nassistnum as 出库单实发数量  , 

 ic_saleout_b.vsourcebillcode 
from
  so_saleorder_b
  LEFT JOIN ic_saleout_b on ic_saleout_b.csourcebillbid = so_saleorder_b.csaleorderbid
where
  ic_saleout_b.dr = 0
  and so_saleorder_b.dr = 0
 -- and so_saleorder_b.csaleorderid = '1001A110000000H8T8N8'
  and ic_saleout_b.vsourcebillcode ='SO302023100901978011'
  order by  ic_saleout_b.vsourcebillcode 