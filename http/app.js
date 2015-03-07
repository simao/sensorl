(function ($) {
  var init = function() {
    $(".chart").each(function (_, c) {
      var $c = $(c);
      var period = $c.data("period").replace("h", "");
      var query =  $c.data("query");
      getData(query, period, plot($c));
    });
  };

  var getData = function(query, period, callback) {
    $.getJSON("api/data?q=" + query + "&since=" + period, callback);
  };

  var plot = function($element) {
    return function(data) {
      $element.highcharts({
        chart: {
          type: 'line'
        },
        title: {
          text: $element.data("title")
        },
        xAxis: {
          type: 'datetime'
        },
        yAxis: {
          title: {
            text: 'C'
          }
        },
        series: [{
          name: 'Lounge Room',
          data: data
        }]
      });
    };
  };


  $(document).ready(init);

})(jQuery);

