/**
 * 
 */

function toValue(value) {
	alert(JSON.stringify(value));
}

function doAjax(obj) {
	$.ajax({
		type : obj.type || "POST",
		dataType : obj.dataType || "JSON",
		data : obj.data || {},
		url : obj.url,
		error : obj.error || function(data) {
			console.log(data);
		},
		success : function(rs) {
			$.extend(rs.data, obj.data);
			obj.callback(rs);
		}
	});
}

function locationPage(data, url, method) {
	
	var form = "<form method='"+ method +"' action='"+ url +"' >";
	
	for (var key in data) {
		form += "<input type='hidden' name='"+ key +"' value='"+ data[ key ] +"' />";
	}
	
	form += "</form>";
	
	$(form).submit();
	
}

function formatBytes(bytes) {
    if (bytes < 1024) return bytes +" Bytes";
    else if (bytes < 1048576) return parseInt(bytes / 1024) +" KB";
    else if (bytes < 1073741824) return parseInt(bytes / 1048576) +" MB";
    else return parseInt(bytes / 1073741824) +" GB";
}