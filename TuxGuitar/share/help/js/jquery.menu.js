$(document).ready(function(){
	$(".PageLink").bind("click", function(e){
		loadLink( $(this) );
		return false;
	});
	loadLink( $(".PageLink:first") );
});

var lock = false;

function loadLink( link ){
	if( ! lock ){
		lock = true;
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
		lock = false;
	}
}
