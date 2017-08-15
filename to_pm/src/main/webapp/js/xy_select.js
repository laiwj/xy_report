function Chk2(data) {
    var tag = '';
    var flage = true;
    tag = '<div class="tag"><span>' + data + '</span><i> × </i></div>';
    $(".tags").each(function() {
        if ($(this).attr("isMe")) {
            var tagSpan = $(this).find(".tag span");
            $.each(tagSpan, function(i, v) {
                if (data == $(v).text()) {
                    flage = false;
                }
            })
            if (flage) $(this).append(tag);
            $(this).attr("isMe", "");
            return false;
        }
    });
    $(".tag").find("i").on("click", function() {
        $(this).parent().remove();
    });
    $("#sublist").empty().hide();
    $('#maskLayer').hide();
    return false;
}