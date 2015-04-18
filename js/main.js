$(function() {
  var client = new ZeroClipboard($('#currently-available span.clojars button'));

  client.on('aftercopy', function(e) {
    e.target.innerHTML = "Copied";
  });
});
