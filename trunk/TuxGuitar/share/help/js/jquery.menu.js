$(document).ready(function(){
	$(".PageLink").bind("click", function(e){
		loadLink( $(this) );
		return false;
	});
	loadLink( $(".PageLink:first") );
});

function loadLink( link ){
	$.get(
		$(link).attr("href"),
		function(request) {
			$('#PageContent').html( request );
			$('#PageContent .PageLink').bind("click", function(e){
				loadLink( $(this) );
				return false;
			});
		}
	);
}
