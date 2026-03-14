SELECT
	barrangedflag, 
	vsrctype
FROM
	so_saleorder_b,
	so_saleorder_exe
WHERE
	so_saleorder_b.csaleorderid = '0001A110000000Y88V8P'
	AND so_saleorder_b.dr = 0
	AND so_saleorder_exe.dr = 0
	AND so_saleorder_b.csaleorderbid = so_saleorder_exe.csaleorderbid
ORDER BY
	CAST( so_saleorder_b.crowno AS float )