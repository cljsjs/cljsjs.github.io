$(function() {
  var client = new ZeroClipboard($('#currently-available span.clojars button'));

  client.on('aftercopy', function(e) {
    e.target.innerHTML = "Copied";
  });

  $('#currently-available span.clojars input').attr('readonly', 'readonly');
  $(function(){
    $(document).on('click','input[type=text]',function(){ this.select(); });
  });
});
