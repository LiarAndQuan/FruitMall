function page(pageNo){
    window.location.href="fruit.do?pageNo="+pageNo;
}

function del(id){
    if(confirm("是否确认删除?")){
        window.location.href="fruit.do?operate=del&id="+id;
    }
}