jQuery(function() {


    let $fileInput = $('.sidebar'),
        $droparea = $('#card-create'),
        $body = $('body');



    function addHighlight(){
        $body.addClass("highlight");
    }

    function removeHighlight(){
        $body.removeClass("highlight");
    }

    $body.bind("dragover", addHighlight);
    $body.bind("dragleave", removeHighlight);
    $body.bind("drop", removeHighlight);


    // highlight drag area
    $droparea.on('dragenter', function () {
        $(this).addClass('event-dragenter');

    });

    $fileInput.on('dragenter', function () {
        $droparea.addClass('event-dragenter');

    });

    $droparea.on('dragleave', function () {
        $(this).removeClass('event-dragenter');

    });

    $droparea.on('drop', function () {
        $(this).removeClass('event-dragenter');

    });


    // back to normal state
    $body.on('drop', function () {
        $droparea.removeClass('event-dragenter');

    });

});