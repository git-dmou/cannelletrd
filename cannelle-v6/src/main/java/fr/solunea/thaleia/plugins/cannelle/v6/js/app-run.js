var app = angular.module('myapp', ['ngSanitize']);

app.directive("deferredCloak", function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            attrs.$set("deferredCloak", undefined);
            element.removeClass("deferred-cloak");
        }
    };
});

app.controller('MyCtrl', ['$scope', '$sce', function ($scope, $sce) {

    $scope.lang = $("#thaleia-xl-content").attr('lang');

    $scope.init = function () {
        $scope.notifier = new Notifyer();
        $scope.localisation = new Localisation();
        $scope.prepareScope();
        $scope.manageFirstVisitActions();
        $scope.initThaleiaAPI()
            .then(_ => $scope.initFilesManager());
    };

    /**
     * Préparation du scope (initialisation des attributs, etc...).
     */
    $scope.prepareScope = () => {
        $scope.interface = {
            workspaces: {
                introVideo: {
                    visibility: false,
                    url: 'https://www.solunea.fr/wp-content/uploads/Create-your-e-learning-modules-with-Thaleia-XL.mp4',
                    vtt: 'https://www.solunea.fr/wp-content/uploads/Create-your-e-learning-modules-with-ThaleiaXL.vtt',
                    vtt_fr: 'https://www.solunea.fr/wp-content/uploads/Creer-votre-module-de-formation-avec-ThaleiaXL.vtt',
                    poster: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAA+gAAAIzBAMAAABhAy6PAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAYUExURfahkvqom6Ftat6ak8CFgWpMVfTGvfzz8OGxZdwAACAASURBVHja7F3Lctu4EmVXpzLbZinlbF2T8b7lODVb35vyDzjWfrSRtuON9fsDSpZNUQAJNBoPOmJSTmyZINmnz+kHQLJpp7dGuMXtLd/uT7fPm6gt++m3yTefQ5Dw7KEQ6DPf6gA9CnO6oCgxW2nQKYbpdAG9PtSbxOoOFxDr0/cmHdGbC9FnC3pU8n5BsEZ99wB9QXKiXxCskerTB2iEEg0XokuZDhXEdGgklP2oEZ1/A6Y3Xf79eR3Md6gqpLP0bwmilwcdABr4+xkaCPfWS702T6ZTR/F2+0IgOPGLmM8VdJNXbHa7TSDq0EX0S/JeZ812BH09wvTPO7P9c0njPhrTt7uXw3+uXgYh3RAdDdONvsMyzFc/UETnj8j0K0PlA9e/DkA3WFMDJqY3FEr0t29Q+ley//mGkRBgL//Ht58M8vvhWXATcdJ5mL592WwO3/9xxnQyVdvX5zfaLnxBh0Ic49lqCedC/Qg6ma+bl7XhfGvgvzLfdF6w3RBd7db0eU19cZ8sbX+ntrtePU955f1rJ+57jTfyvl23u7X55tlE+sOXf8iU65e2+4fJ5N5i+tqgu9k9H0E3UO/a3YuRgKvNdg1hEX3fdqdyavsafDE+nmcv1pYZSzaTn7fbZ5PGfX0hI+e79e75ane122x3tDGu0FBI6n6p1ypP349KsjU4rzvQdwemdwK/6erzTvT/7QLO0jsRhvrU/T33fleB/reufz52c2axezbx++8D0/egtwb0zWb9x8tm+9wsQkq2D1SkY2x5IIsveRK5jcnRtyacv3Qx/eXqAHr3xWR2zfZfk8YFLKUoWK9d1N0bdOrStn3u9nx1+PdA9+2ucwCj/MF+eplfi1KWXNn7y/HL875a2x3rti6mt0HZey91/403YVdp3/ta5onpVxvD93az3vdlrl7bM21ztSZTsq2DUvEW3tM4rsGemOLIrHrcwWiUB3TqfX37jrrZlq6zFtqYmUidJ/rQfG6wXko9GNKn+Z6o7dY/Iey16M9+M3i+oOQaOTLbHnsIYDqAbr2GCZI7hsqzRCgHOuwXxtGgWsPJ800d0JdIHlqZqxmn6zzLwkyn/TpYOMo7e6t7m4Ti726lPF40qPru1RZmOh2WRLItnLEl2r514zBhLrcswsJs/YOSTH9l1SLEFlm6caDK77FObA5an29UDPQOcwq+4v0EWw7DLDGDtGeKIINdl+WYLo5G1BTqx5G6LGeemeX3AmhOoCvPr3EJ0pWlfIY8zg26bEI8yaSqy4RBc/zzaMUWZroIO50iHf0/ptjjsDqGGM/6YkwHIeit43pHU2Ify3qYHHJSlvVqNx78vxjoiiHd3UhntZUq3PuHU4R3Dv14iOT56h0nB0qBTgXl/cRkHGDn/gwwZ6O8fupYDPRIzFM3rfLl1Zy9NoBSoLuvFMc8lFq9qouzkTTduDgn0KX1WpvUspyG8j5h1hNH1nCetjDo44YYfJphVjXpNI4YQeWzqZTpbE2jst7Ahr2ptjzpVa4sA0oxXWRCqv2uRUxH9jkRPRD0UesAnAoEhxtvbreeqXlKP2cpBbqstaV/Axsn9gRWmw6d/MjfE9pCoCu0YMUW9kdhOQveB3vVzECnAHaNdiTZD/PFwtXDY+24wfZBMAh3ry4szAn0fs7P/atlNSm29F0zJHMojNtCR5sb6NUm4crH5hG/jHbxUqCTxZtRSd3zIzuvkiAD5qOgi9V9FgkV2tuL2aq02kAX8CZtO25irgfjIELrZ5gNcM6bu9tB9y238bQbd3mkv4ZjFwM9oLg+fr7v6HASwUvYK+Pp3+fEhOf86m4D3YfobFV3ijRegI21fIoxC3+rCulWpgss3Vd3/pjyy97msPouevlGFsytTBf4a7cfazagX3svIOKSb3Ov93ujz1HKV/QVA121M2P1hPF2JMfIKCbhtKJY8Gh92M4L9NgiHZXtvoRmbG0sK4VibkRa5PgASoFOMlWqrjOztD/vMGnGETt4Wwz0RfgFDH0lgWVDdXtBd3+WSCljjgnFQJdc1yjROZFNx8f9/v/VY6DXYCmCZ8Y8Qt7R2oOtpF6ju5vVX+w4p7GqggtSvhzodPryFC82+GV/cfYE20Aubn7/c/X4M3M8iT9QWxD0YCNA8Rm2U39aGJp/a9rMjI12EigHulbuzhlRPv1ZF81/Tu+G051fzEjzjJh7gM5eLqpCA9bwlX00D1+Xi2khnQxKbVsOdJIt3/S1I6cmPd6tVj8zPegIHVeJE+pSVtvtoIeaHAYL5dljn5P2K+rhj0bav6u3BDCDTlQHOorbcZlTYIP5XxW+X8DDCjUwPQQseCM6pjXM9Bgma1+qDO/z63oPt6I279aEJO/OJ6Q0mLRkY7/I8elm9fMsg8N0AtTlP2QfFr21yfxpoSzoEuwm91Kd7nQb9u7mkYEChlI4MRq5WPY0Rpt9E4P+lobZ9uIELB/9JXMqd6tHU6lRRH+lxBsbAcCIOxQFHdiHoDxwVFLET7g73K2elgmOn8oLXt3zwLTCTBeVGuVasO93Bnpg7sYQU/ioX4V2eGVKXaCfR1CMbLyjDtaDYTwxL1FM+pRoQDNjOp1gzg6j8qjxWU4wPmIu1Bv2OyyLnMiddVbE9P2qmSAO0LurcDHW3N08sd77/1hXFNjqCm3JrYkjOsDYs0q4cc6Hab4h77up1TL4FmuOAPWAHt6EjcjjnEaEY4Lr9bZPxhHMy0Ru9rhSqAX0cPjAchuUzND2vZbTuy0e9Hkedntsnc8C9Zd3ybQqyY2J8ejgw+N9S5BGnDnsEw45DFTHdPY/c2rSGNy29zAZYMZb9/Q524+OcS7HXlPkPOn2VAnogKHoQaZXpTtP7G71jfavBs1fpHPkE6Brj+nuV2AWbMe9FugQCzWOopguFYRKmO59ufjurY1Ha0al5LHs/8kU6JiI4+kT/zpAF513NKLyXdGduHNYQsaNvkfOFnSeLDZJwz4sGgNuV/eFKu9563sTVaZP5gFJjIUnSZwQIkwl6+g/1ixBB4s8YBaS7I+0+pV1ESTH7nb2YLQKQCdfT+de453C3V/JEfAhS8c9ZcEJFTDdy8F5ENJJR1TD+XW74nT8ZMFoEH6oapjuL+5Rq2ZeF9iBjzuwvSszekebUpc3zCChnjY/0BtyyHuAqcROs7h5OqcWxgDIGmIxNcjJSSxK6bsddC/YuuYxRaZSgaC/t81vH5mkLOUo6efeci2MEgkYfWc2VMl0Et7ZfJIUgKys+rK6Ti/XGbbiTJcUbJHZuDQnQCPuWrUWD15E4e0gGh4ElYFuaTLgubxTpKODN1T9g98+LsOg5bHYqoAkdo+zWlC44jjFnbKAThJpCrmJjb0aeuxh+rvVfebnjIun3ifDelmmU4S8n9uGm9M5S0dWABLwTObeRpu7kshfGHTbBfOYHbt94m7XlWWC2Ik75cKMQx0Jg06mCtCTQzZ8tE44Tt9vvp1gXv+LeXjEa4qCHq7u4LMPT3l5eCb440llngUz7VNh0dZoVGwx10tehuZ+Fvc/akZziYDo7tox8mE4AT3lOpnOjusZYI4+VsbzcBbKdLz5Rb02N3vLTNkogDUFdSvo7HEFAfOq48IWuNdt/ttZRlfrc+wTTuYU0wPVnV0uHnjcxU03uQaiI6rUc3H+xY50thDokpAexXTwOPD5E91+PMZkcTz+Y44ej+WKNxOmx6k7DZZde9nry+qemqQbe3OclY5YEehT6/tOmM4CdRyRd+dw8PAk02LBTXTZggDULe846MfFXCxMHBjtRF/GRllOAp/77bw4+bNyTA82iXWKja1WYOcA3UqBENM//PIA0Pv1yOgAhTO35KEQ6DTJClQO6a6m0Bhcn1bc5N10gOY5ga7bj3OIGgUQ/VtWBiZLBgcnXgHo3tkH+cyls+MnklVatx4L3VkYwbmgj8yG6ZGNdxDcNtnefKMSkHFyUSkEemhQg7Mbm8MMD2f3R08vvLp9XFIM3P4VuELYx4DsclZMX8hZB9Zb6HAiopNE21mMJOpHA7aOClUwHaf8+oAViaXyXN4nx/o0vl5Gh6Acp/csXF9TmbyPVOmLAFnEsSjmdcTFw7VeHGeFobxu9PQcGUqALsvj5LyDcNBVanQUJmbyW6K8NioIesBi8B7R84A+bMaFwpS+JEOJQ5Qp1WVM37dmtIh+dnS2En0pL6xYATxM6j3VxHR2m+PYj2NhtTMOum07bcZheoKm7NBhFaArtWbs07GTl+gB+p01opfT7Hk35Yago5e3Rq6aGd6nNb3Hj6cKYMIPBToJ1DncPOy8wsb9tpDXn/afHcYp6D4SyzhV0l4N070uy3eKjX2zlskO7JOQghiwF2qh6fuAcCyFuo3pPIVcgDiwl1dPjIY31z44YIIWjNizA70Q5invHCFlEydw+5grLrPSOPaBsJKiTQq6ck06Ua9dF6FnwPsd+PXn3rtgSao7QB9vzslBZ8fl0chBIbJeY2WFVtZ9LgR6eKrpnP2eftGB9erGtAYefpGmiAsqL+nLIDhSANMzHUNA165ORnZY9InO4YXV2KeYnvbseYQZMF27zUxj9Ro1v8EGeUGfXKmEExVbUG0UCjrcXAcoNEffW66Wj9cNeqAkUgJ1H1GbO72HPaMS1NZAz7EnlJnpYRGt14+TOHobRvXFj19p5JRT9dlROBbUwPRmhOn2Je/sYV3XpbkWZeDZRDrrZl4WnmKOTt7AqDkfKdfoVmx9S7HVuo5LI9dp9Nvu7N08t+yC05RkH3HAVFJTNej6/bgD7FZHwodrkkp1uqlQDvyFszqfi7ZnLM/knRK3DiAUSfvEldkG+JL9nsUcTmMV0KYk6PbLZh2mu0En26jw40kZO27y3/JY28MJBE/fPoLOuqBbfWlxc18pu1nhN0r15Br7KxRZuR+HfhdG50U6qUgvJ3MF9CrVefirRanePXKdPQ3JR9zka94hDHT4j7wrWXIbV4KCS+8OGv3uCoq+JyX53mar7xNtz///yoCbVi5V2EjNyBG2uwWSABKZtRDL7Gx3RAF5oU9ypktwc5fHucH8UPx4VXeaQRMPaVjnIH0ZW5Ca6cK2MVcnuDH94c7fv9LRjPf6FOFGwnNxtVqm1xeVrs2ea9Y96jScgnV2vuHBZUowztQCNp3XRZEitu6jbrc6oB9lgB51JCTYaIcbDytmunbXL8ZY1sMp2HFYJidSIIImwHsUjHkeC4FODK5qMo69wdKvG999H8MnpzjiAJ9xxOdEGNC1WKB5Z4p6bLPRZ2aCpmCxcJp1Rf67C+iaPZDJTb66J50CzZPC5G8ouGB035ID31cKepZlyrXrhUwf3ivQDxfE9sVewZMbBB3TeVR3rvHq1MVt5gfYeEbJwHFOhqHXBH2q7jTovLegOMwtlzH97Wu+l4Pv/ZyA8ljcqG9mxRqSLGwYP6UtLJwch9mAaDk4DTfVl4jpms8GdJiryOLVZ2beY6bDw+Lqkb2n9TCdxoFT02W9Xx3q9gHma6l3aORXUG5KKLFR32jBuch9xFYmILo6/E4gyOSs2JHGYBrQOTuDIUjmnZtv6szg5/tc90La5+BH7xPjAyHG1KKbE0iFrblkVBwoHNPN08IWyEAlNkSU3oIszXSSVkrJOgRuAdv3P5uVfJJGAomYriRjvFnSNHJoOsK0p1WR7Ocuov9NkZF11wmdXt7nqy5KwpIL6JtLOg6b1Xzw3B44AU1zRdOADqG6a6OfQaVgyqUH0nEhsAJrVBOP/CQd8OzxkEbe55el3QNXlzfS4UxSpj8GbHMramhhDXgtoy6s+VUb5I0V5WA/31mdjMU9r9d05ET2uXbeXeetSfy4ev6EG4ek+1BQQO4GYf1SoI/aatVegYhjuDPpU/Mn3JmcQANEOVw8X6XSgg7WMNSuQ5rXmnY2rPq5lxMsoHUFr1PmpspzFtAvsYugHDhXTRNF6Z+/Vmpz42tFtkLQNZ8lTqDr3qS79LggVh6YJ8ec7Uovj7pM4VrQnVoti9L/KJEIT4wQSkhS/jiFdw4rIOiYH4RiGCDJL7aXDJl0L8zghRakuHptU7pCeXftbAnTaQGTHv9VG0JK4vKgh07C0lfpppqMfoaHekzl3inYiFkCdJqP2IaahSBN0cNROmLmQ0g+Cigi3VfDdLrNwrIaj8cZfwKmT86DpSDgh86Yv1YmVpJbYEVsXkzXM4n3JfCElPLw05uUTAdzDMYdvw3q9HcWFalY106ctgrBBWpdjhyX6fDz405/shB9Hcvux1MQWiPodW7cuPWCoDrWpGvvhIcfoykesIwyi4NOgWy6wHmnz3fXfBymi1MssiKkkKRl+uwRhB4mXQD6xGpVCk0zF7c/urVYnbzrmV6Dv/NOXwOr5RxW+GtOeubm7jQjDUgV/qm1ge7KGkFlxrYXeqlZUM8HBitwJ5qtCnSXiI3koIeZ8a5XgPirMJ3Ch+nEbUcr6vScmoH8gY6Yg4khoib11sB0PDrvEKOiBCb9B8SkghsVQxoMBOT7AqDTWDMmI7bphKTi14W1zGHFqXb8O5i+ua5WiNqMtui33z72srElE/0/H93HmGtLsiETHXTDH53qKaxnr/zhM11FW7lIC/GaXpXpktwMPJgOcn3FhrGeJt4FGPkBC4SLKhXojP1TImdh23sbkR8X1XJTGrFYEHRWVbRjd/Gdd9CXj1qrBJDQfwz0LGYr2hE1uQMFL4ymOAgincakBB2TraiZriM2on3uz7/WwVSwixDrLrQiqrvKO0TdI3DeP3eC/qOwSK8mu78eec9mtx4hfz+uP8HjldeYk3f1X9KRc2e6CXw+EwKUh5souACPJDOmBN3QM12eGhcw/c1vY38wIy4El/WgWZrUTMcUW+OH6YffWjCYLnVDEhmYuSacSVqM6SSI2OasmORlug4OFGv+y+CENwpAXhcLvwrvHV1FlKiHIQdd8SZFLpGTSZq6Sb/ChcYqoh0bqNjqrsRJWPLVAxKLeoLIYkmm4wE5R0ZpgfMOV7QoPl1joo3uT1LQwaBrzIitvfdpaguK8T7XjFIkFANKiLhTuJMiTn9kOsa74tF34S9u6TaKfI3UDIIUuTYYqTw5kZ2JH7HFz7xLUWE58Nh436a5Bxaw6cSVd6G3xbHp6iHzHiRZjuGNo7ASHVgqZpPUw/UdG//FqvqEU3fCt6MhvRzR8F4Z6A0wLq3lO++BlrGt80NrBx0Cps8NbKW5Jv34xWQYxZJcSoDoCzD9JnR07Qp2Rd7iH9wCh8Lc1AxeFfSpd+LRw/QkZ7F5gQSXqM3hOeux6Y76xtlZqvXjfv5OhS28g7C49VsJ6MwwnTzCdJeFDuStrYFnUsHTzCeYRsHvsAS5GdlCh3/PuS2pjbo0YnPodXul4tVjZsHq0lGbwJ7Tq4CO6RrbwnAb+5pbj8s27zHgpXh8pvCVW4rpeGK6fs4SU5hB296ZvpIwVrTNI8IgLi2lXsqRG2ghQ971JUzXLh04u0iYWPdK6bLPVCUh6CSy6aH9uM3338KuIR+xRWogJVZgPUw3Tg3nZ2H/z9/9GdMl4GxrnXeSDzuGVuK9K0emCxYv+u5HQAnoGkhDMJOqWxz0y0mpsx1ArnkGhzB9FR+Mt97vvIBFQEcwm24Vggk6fcK7453kFmM/I4SUIJo+RmL6fQwBp7aymW5+JKMjud/EAXhyeFIC0MEZelpN1ZLGnQHu9PuvoJ3ndX3MU1pYz1pLGtZx1jt/zcUfUZcHdttWNjVHrQH0p+WLxB3p7Gps/yTu2GA4I/wdEoM+erLIc5zOOpCNoe/83Ix7/4Pze8QbVEL1SiPvNG/T3RjCrkbieTNi4B58OYQeJ1iBvD/v+cjuIwhB7xJyO0ZlHteCyA9CfQ3DERd05gGEzAHy7OxpTpzeYNcl5LwTYJjBgJmjpeCqT4H1MYUjx9nvgdyGbJuFfQ+slHCCAyEBpXUynQ96vIitlffXyMIiVDQ/mXuPCzp3u5aLH4cYQ7YpOLchAULKZwyzD5dnjBijBZiOoRQ66fnmlGV38hFJpnXqZnWz61bAGffUOz08KnXguBshxtJK5F1zqwsyqvfoiM90cepdda/9TnkxLwJU5R+DKUUy56NQGs5IoCRRQS9nRzZ60ImJu+3ckiQjVrNS7w+fbdWiuM3PVTFb+lCc8/fB+ld5XoyBM9AxWxDjcd5mYUmm4zYhpzlxUe8kqBIymy5cyEZ5nu8bOC2W1fsc+fKNpmII9G1RUvXOB+Zgy1YI7hU8nZyTFHRMJeQYzSutYDpFbLLUOx0+lKkaptbXzm0/97bvSj5LwLsmEsQN9ibqW4I4YwU2fXAOBYal3Rr0siwhzcJuZVnYmm3fftX/sRWj/cwQedvZih2GKlz/ks58zlpp17SLEiTQK4A+DH7Zy3vTDs016bfHNIHhHBcdwdV5BvS6vHrbGY3D+zBzN6rgQNH+x9oIddyNYhkqAExi02m6/oo/QHSfjgW38u0qisNfok6rcaqxVgWD6eo0yvQa9GzPDrrrx9khxAu+6V8h76UkjNXcyrcF/7eTaGPHdPvXMNMfrmhgOo2BbvZsLIx9rjG7TfTP2uXdV6YaprPWKdM90xuZrXhMHwPx8Gvi24GRbJ+bHXeh0jn474LelJTNeqeLvLdM19M2tdYEM9SC8q0AJ2S7BV2NMJ0kaj+zFGuROB3P2G2c9lXnv2TbSUihe9BvmE6j+8U1XFZq4But+uQMT93tc1XJ8t49s7EqMejgM312Iwj2XFjhS7YL0y8e3XT67sam455x2fHjkjPGNfoYaU7D9GNIm47hkfFC8k7OTFeipQ7KdFjTLehm0qarQe99c5t0UpMDmC4Kk8CRS8d0YnnvM4iL19y2w0kEujGDTDfmNkt0z3RcQrbmTY26tl2XFx7Xdv/+yuwh8XSJGkJG5RvHUys9QKf5ugjidEdHTra/vznuW6xbFOqYi8558XG86tHJGuvq3L4Ru2f68Vzl9ouuKFVFeXNJfv64q8gp/7g4YtUZWcv0qUlmCIK9Wp+8U3DQN3egz9z/WDUvWygvzseW6epk4bKYdY6BOtofz/UvLGYdN9GCbg71V/0X5bE6d44cVfZ2zTeXUWBHS35uC26AqqgKFLZu2503kQd/hxXadFIQn38oWMkmYMi2qBlszMkC1jLd5B91Y7YWdXs7HPMP1NP6Mn1qIL0ynQ4FMq0z1YJJ233z5kxtTNW+ls/Msbq8oDf2Gir6h2a2ZFG/nN8hiNkeTmethulZ9Dhd/c0eS7Q5HFt8Mo3m5bYqqo+2Laeivh3lH/3KO2WqfZeRa0A/9IBm1MhCDfjJltDVvruipOaS9uqqBr6RD1TWIJx2Re8hxMq/Lsx0ume6kbw/ggD0prNFZy9eX2jr5hhmk+/7fZCq0krS4SaEs0rfuV416NscN3rR3EqVtUvwrbhQLytN/xbWnK1vuN33QaIyeyvvhuu9w9HG14qagumsBS6QmzE205kTZ+iSMG/f46lD8xo+v1jhGiG6ZluARtYvoB92N/exFKb6/XhZ2GGDW6z6F3JtOqAeY3SoTw8zuzvvPaCzXnbJny5lVK5H3hFP3ulLM5lzx3TTgv5xDdOrjfr+q3k01ZDrjbEYbXct+G+NuGvgAmlFG2Nd8rdWG4hQKluRY/djltf/NJMm6meqtxr0nunw8NhVA/T1T423/Vv18Ou0oGMEOzdDxc7IcUC/3PTQh1XW/PaspmPZfocuNjueOw/dxvTN1Jr6Gwuf1se8aEM5W7BmugW/pXZzST3Rizp9z+rfU50U+Ie8a1tTVFfCxPIBQuO+d5B1XyBz3yL7fr6ZNe//KjtVFRBt0Eo8gL1Z65vuVo7589e5As+KlA053GwDsydXMpHdf50h5yn/Kp0OsyZcQkDPMu+bOZ/sWHT2mi+K/dgCheOpSLatZbemE++mMWCdkm+9m7bZkaVWloVpSkMKn745OuvAeFliPNY0XdynZZOI9R7ou2KYdcchojnq3vFCvFsbRfXHMB2/xGaaLfvp7THtnOx2Lza389m5AjKn6MumEPJ7picFSazmmFjgQLwt0obKnxzoFrLMfeFOnZvkVHCR5SQJ3OTIeWbtrFanQxz7jRPsVHC2ED/d4LN0ugzSjZdo43neg8m23vWlTs5ed2RnR3zHNRHrXepdLkKQYAL3Y0caueCcS0v6symJ6e4cRYbOc6NYRFKVNE+6KE91sEmKBVvwlHy/YPo3iL3P46erQL+4HyYbD74UrTPeqwOdo3B87OUzIZhUBy/eqZKypECN8J4gbdigo4nQmYdr98Ouu5zfhlL1tuLvHO1JvcNmm+ilUuQ+T+1l046yE+/4TNDPyt5vXsnyuNM/wnTvShNAWZE58e5TMA7VhKx5LqGsOBBrK2lwYudtvRWXjex4GKbV+2Ia26DzCta/ZQJAOpJlw3dk+hXXA07zLrm2FO9kM7ca9LCydxHvwKCbpPerJTpHFnlXmi6huAP/WZVsqjeSLCQl4OYO18gVZKWvJM4Gp+P4AVbb7qmczDdwk+nfR7x31juGyizFvUsorWO60loUpnNqFbpoDXagV5Qz4SQpNBx0P6xEpzOXC8MpdOHsjuKszvLb5DmHZzDLnBwwJ1vNwdx1b2QFx3EU+XT8FqCnQz/95krbUUwP63XgcWeLjZjuJbOIevdPzW643Ysb58T7ivrYqj2L/y73zrTfiZ9us7o9iqdeUJPOwEAvm67B8WObSOXMXRE580ZMt2lUHkHrp5v6jyKifwE6dNa7B9IUyalzAWqCnAL7RsS7EdCpCS5vGu56NPuO6UxPS4d8mjPx7j7sZwl6pk+Ld5x6hNPv2Vsx/fLpHpZava7TRy9j1x3TYVg5U3Thcge5E/H7RDptVwOmG2h5K4ojO96e6fQ7HXKU5lwcML0z8PgidtDWFCfE0yTTLKL+AtBBgTpCBNXVOr1WGnIwYPqmYzoMmO5Mcap9KI7CcjHhVz9IeVW0cwAAIABJREFUp0tBI6XZJeRpRaeXZZEapCqMPaU6ymxAZnA7+UANGYvM9IF4V+GOMSJ+LqbjRUROT0V8ivWOY+I9+cp0Axtf7UJ1z8YkH168XzQ1uEmRbSUiRyFYiec7IZ8OXTa324d4AhUmk4WRj/faXgw6PjL2ngaAHrL47DjoPtKS5Z+pN71bqqRxoHMYtj7vaHboZgdx2RIKwfKXds8B2oHazhopp1uNMD04hbo00PGm9f4kQ26S6XDDT9/jGeiQ5JaZXu1kilLSzFncDq71j4RB79lvOOCUZKnPp1PZBRn6dAglXk9Mh8wvZtDr9CmXDa+PJiyZ6Vf8dHga6KYOXIhi6w1h1uPrAdNN4WAnqeEctvy43prEfnAY1nimsx3XuP+bFk3ZMtMpaotcUZsfGdwT003145TFZ6bbM+sdniPqXw86fGW6fXLs/apOh3HQTWesd9TL+QNqNbNsy3nwPnzsvZbUHG2M/NFZ6pZ1Ouwoi7h2kKNl0Osu9l6WDXq9Q+V09s7KGXgLpkOPunT9BE7kNAZ0xSX6ziJhui9PFZ3O9Sd7p9cdoJw4+ThwlN5Z7yT+d4gUcwDYdxM5KwTG/GgI+u2gzdVQOtWJFMnfcZHNBdO/dZYtmZHpE+I9GWe6469hB6ssE2Cr7PODmS7Bmd1wQsOemV4dTFc/J2baqYmFw/p8GqrEsteDM28JOobqdLxxoOYm0Ov0EAWy311hOm1OVzuxbPemZzqBLqZ6t9HacqLTpToaB2F9j6uz9NAggK+VszaO6bg40FXZXWtBowgglumhK4rtvjIdkqaPw3vopatlvWVXu0KeFFzhyBgWNRdROKY3fp701O7Fu0R4K667ynbGhiRc8L3FuzAdwmd1sE6HqxYPjDO9Q0GYzo04u5RcM/eJZdBZp3NlhakOcpZ9jlAdmOk7Bt27ZjxPMt/6wJ0OmDQSgG/KWwkXfEPxfgW7OI/zXqbDxHDCOej+Lypvc4YaQ8+n3RdUadoxnRV9lw3PnGuO9ScznUHHXM7cdAdKjo1n5E7qrlaHbMx6f8arBBbAdFkmdDjyyoxL+iTx7pmOZj/w0xkZ2BXeqU5SW31SexM4plNETorqoOEuiaxwXrqUS3mmS7nUSs4loBtfLfUpZRpSGOlXlxpbkUvhpYF6xOcBHc50Osb4ngHVsDE6fd3khE4fnGGmSy0NO16evDsx5HxBXU3VL9TqSJWObeJLKTh/g7DxrS4N35ZlJ4DIL5eo9sWwwiKKwgtiOt66BZM8tRr2Si/b6FV9EWyxL61Tsn4likYSZzyueXvkxlTKmrYOdMyqpHQYUidD0Uqvc+oNuZ0Y7u7zNv+U0qjGyw7qZWK68wc1z6gA6/1rk+8i8+lTLwR8WsLFRjG97bLTib0U71Iwt87z4tNCSrK8keAMgUa7UUWN1MkURpi+EvO/cU6cN1i9/0Z/lnxCke8FMV3T4QLvwPRbt2C7iFyYAR/AdKujuH95wHAl5i5+lnnQeQpQCNEk2THpkqpQccaMdH1ZlpY/3yUC+sku7Fy3yncUJtZIc6Ps0pA2uSs4A/guoPtdn5dwCV7uvTpIuk3Mre2Q6V3FHFdKMWBV7ZmeyISwsnzOPi9pRTJwePo5BNTnLH3rB/+3r5eiGDw1ON3dtSpxnsWAfm2RiVjxrgbdBIr3zW6E6RWDzn1nstUFISZMR/42P54ernHCm6B0rsCpo73OuapKQKf+NemHzKyUUX8Jw0JgTuVydk+4Qe8ce09jmK4SKF1D+T7py965GWm9NZtcyh7KOkeg/RqSzV1f47GLtdEqI7w0tJsSmbQ6Ii1gwjG7fv0xWr1Mgo2UsW24yf3zHqb/nlBa591vs4KO12Pvt6wXddfq9Ve44JdrmZWgVzszHLoJQPRvPp0QlvVk+Ifz3daFaY6Jr2pdeyOudpgbpLXJGOR1zmXTNCdI1a+LXgP3i4i6X9aF+y5r7om6bH4vQ7yjgrCUhsQrNMT4exe9HPyypiaXzHi3UJT/zNHXUi0srRJFhAduY1j7Zga20XP/rRh//rUgcoicqulPWXeLiJK2d19/Zt1rRGK3q+lEXJJ4N8/U6Wkc6LCv25aKHlpKg/vP6pbtcvql9bjtW86gtJ+nA6WCwkvvuh187G+i7afRevBupn17tMb9c1eAXRl6/O75dHEMLlaMjBzYHn4zfcf27MfAczb9zB440+nIoZMEVbjqylae9IWg48Qte6bj00DPJhYEDr6ijaXgmcVwOk3U+a6YosrXxC+C6daer/kMyURzGwZPWBnWqFetmq9PUD4kyWVvR9Ag7s3Lq+UxfRadztExeaky3CKinvmTq9OjOgD+iA3jmP4S0PGxOh1DrfexRf4xyFEIEb44fRJ88iT4+Us1ePOJd3wd06+82QGTZD5iPnozCtBnEO8TQcEnW+8n0GFGbDFGbExHkEaZvl0i6NE6PTaELG9rqv4k/xfbNwP9vuC7wsCB2ZCCyENGaP/fg+rMrwIdrvtscUpQrdM3f+ZS4w+fBdfufjwega8GHXSEDWA6xoRhAzsY33XThpvnFe94XxxWrdOzf8+9ZwwUr/geoGf/oNWIkBlAH2lb9UEqDBR3ankTssr/w5Q7vvw08Ff3VAsw5BJtESXE6nST/cV7YdEERlGPG4ajffsQbYXQ0kAP4Zk+OAP/IDyEXS+z8TFgN1rAX94o/1uVwjEvAH0sd4IXoNuokdXGYSEVCwenhhVCxhqu3mXctICQb3HqL5g9nX6veFfAoC+SU6XZ5nPV79kGfNm8F+gm6nnV4t1Mgo4PE8ZT/DsLoGLYTAuchgS6fcywPQl00DH9IaAHvkD9Tv5PlXs/0vEbPdf8NRTqV2kHMz3onaEC+n/+PM3lvmNSjNrn6njc8DT8eVr9Us2NdPE6/d67F9Cfk3GBR54G4o/27qT5+UN1ny9jOl554JEORniYeDdKwTdedj+PbYdR88umU6C/K9NHJaP+Ns7sWogc+CVKiZ7rYI257qPgoqx3M7UzKA5UBd+vtn6g4qIQz9HXbamy/vPpTEcd6DbqKfWlM5dNXjCPkI64sr4o1iqTbCZdCNNteFwqyJILeoP6xH1gr+XhabMG4iL5smV/k2UwXTOkt5iO94fkDPe4wDwBuFddKPttlsN0VNxF3BDpyyiUNs49uEZhi1E74URAzqqm3CuZjlMPE18kZ9S3MR6SU63IFVi9hIr9bi42ADoCwNICcqn+dtMXFEH/eq3UxnBD7N5NXfO7CENuvEgOFSOVan22smcBvAB8SJ52MZw+9ddWBxz99eVMh3ujM+cPoGS6WUpp5Lgl+RgJYEptVmnBTH/U7dtRR101HRcThFXNDHM7NoPL8dO/gq6d+1qdbs3+L0YNOcwmB8KpPn8B9DnocGfwHceNYO19gE0nq+RUxMKnM/kBc0YJ+kKYbsLFuwgqLdPNRfMHKoYelgSo5laz+Wth48R74KDp62GTn4dHiFlMYtYzfdD0uDEN63/tQiJyoBkZXZYN/8fdlSW3buxQorABdOgF3JRL/6Bl//vWK23Alve/lSdq4swG0KOjSiWxLZLNPjgHQ08hoEPAhKlFA1helAmRCl+FZvaHl28Sfa8I0+PtE6oJ33fLVRwiq41hpGjx5K1j91De2LfvlVmRXK28g7Uip55GgYYybIHQnH0Xr9gKfPxLkptCHtB5tQEYoQwblqhzBLiwjliu3+fm9BdaEjEsK9NZkqirKCF36sOIOmeGY+XHFMWAfvQYmvpA98i7oRQlH3GBU5cN6ISTq7YvOR64fPCuA510eZNe3+H0bxACbIzislX3+jSd6gQd4wZy8XM2jkNwNKAclti/n+9rVysCHZVM37h2vmRI7tP/+UaDluYPy7StfPTcJScFEty0Fnlv5l/muEzvj3KFlzPUVDoNFv15sz/+0G8L5BIn6sAXn8firo5OcZbX2VTeraHHntIX99USCezJlQadZfK+/Q4ys+1vDuNZ0GyGLudQq8r0+iX45dN0nbyvo45easpa0m8t5Uz7fwsrZFFOuPba+k6lYWtbncW3s4DOMtDJiIILDN9ZL7naqY6aL7Fe+6H35NAfOAiShtfDdOEaFyvTr71xnyxaOGwziou31fB+JmlX5wHdW3W0z3yXM715ebWF7azhvs6sMNgannnbP99EVYHuf2HrrjPCd7gu2c9N8pUhvLjzX0e90PXOC6oEHa3VmRglOSlGLCSlyP3msjO6hKm/genjfoG1kViOpu/UZP+w/stB2aD46LlqAjmkbWAwF9PDHS7Hub8pJeinCwCLSiI5Qee9rENjI2gpyRnBrGg/wV1baLf2DeSs9bgtFNng03nnf2Nnj0mg5kaRylhSt0s+CqKbgKtF3pcTplBzZdQVtL+G+dPovR9uidddaUDHBdWtwZZL5NQ5cSzOwV9BGk4ugNPfpnrQV6qpViO3oj5JuXCfyeivtG9mcBhuC/56XCcN3rOAzjZ5X+s6zB2+WxWe0xB/77vDkiYumrE5JV/XOpT9PQ8uN+p2T5+sQCtffp9L3iUbcNlLciFOPXzLGVvcjVER7z/ivW9dRUy3r2wCnb5jdHpjAHoR/P1N3dd3heXpZWwEndLJu5XpBqduqZejSpMxwIoUidqTMtLgvRzT0cd0XivaQWj4julhsLEVBS1YlF3H853h+FVH5X0FdBbJO99m9eL95+5m0khIIbabNpIrXaB9OcuG2Gygv3dhTOelEfO6T6cHw2/jCLS2ekMz/+c/DDp0b9+UUN1DQTcl6jQuQnXNQHlIlbPF1fXAcM0b90Mn3N0/vUvXgT5Z2fRwV4//Pqf8jaFry+k7rgHC2otxeZlp3hV07eFTdgVUBzrNAtNhFnd3o3X/CxgL//V7EBZh8NoBW/MOZAVlOXUwuLgjtdXEcUovusrF54GSY2Y3s+kCSn1Hu7iai6x+5ELu2r6cm18EOm4m6j2vb6xe1WV6SAGpQOcmzUdUj08xLHt9Ifj49jSMC4HOfqa3xuqE2aljIpwxhxkN7wOnP3UxnYPkfRvsQQUoVs6GUeEJOfJDdHZQ9wx3xZMi0wfvbqO1vPpaoA6wB79uUB6OgQRryuNBT185A+AZ0UJ3/HLCW9QXyDkj2yByzpbK8Vv4LekPeDtTdUwXt4dkowvWQqwhU4+OPwc9Zqs086f5paDD/gV9GbZttgZrpO2Zpv+xC2rcpLrzfsbmWYPNGeM4pzN/uC8589lGQExagOIYXV2Wf5afOUclQN+rMu4yvYMYXh3CMWaLSbDxjyxq3GSqFBbO2LTjWsMVOD+JEJo2aijHapJiUoEO+uyUZvK7dL1PX4RaPOCz54hRKFtAq+MdHEPVWXsPjmVDO6UZzk50bV6sW+c6/n9qIphhSLjGEtnYXwptlwy5S4eSTMeN4rt5ooOU6gmmRZYQfR7eu7nuFFmPujslHULCa+GYi9ODg1YoMaFRjVMZJ579XJbpBtDbfZ8eccpUjnocqtuA8+5on1Hv4NK5PqZLxMfcjdIm8Xa2nx9bewLfzw2+jz3IXDqXBZ33wndrn2rLMxgFLNbKxMaKGrbpzs1yX75id1JOposiOY5TfgcrxQz+gGMKw/JmYpfO5eWdt0BHk3OVLtBJKM9l6jRw+uS4fRQJdBY3ytyr0WsHuSD0j9KjKEtHmZSWl3cOydmmHRM4hct7DsNk89hMBiHiijxLhzxMJ93LQcgylCT6Hitmx5ip4Xi6PHSzwjvWBzp7q+9WqTWMrjLGU3IW/2H/aGd1U9rDX2nr6mL6iK0kwoFXelFIdBeZzxxG99CPfJlDpjhODfoK01ncV7rRVb6iJdkUiOPX6iIumYa3M1QGutaDhpTkSB/KcZ6tAxJGcY07vYrbmp/pKHghUoC+UFepx4JI3Z3UOnj9d4tf01E8rJrJpbtGucBgd+o7x3opikazHPUc3s93PDXYIvKu7EAJETH0nUDNN5Tk7uHLWQyWRh/fJDWhkkwPOYgxaaaeY/0Zxn0EiBK23KDrIATl5BkOr8SmwYeXP3GKaJAl+wtxVnX3gc5bTN/WS46XtAWROApm7HuS12+4YT0TxhLC2KCztwNchkJslTsO2SJHPP2hytS9Bz1Poq4rM4bouPYEDkwK/vahi8WIPgRyYqEO4mF4zoaxKJijDND0I2wktTLIDnpIITY+6OZHsIbKrEy5Lf7sQ67uTWWg8xS39KDPM3XSgcA5q7F79cz2p6stYbuCriWVmx3Ly7cXZgm/IDnVlXrAsWBeeVdu2vafc/MbQeeVptk119Uav2OE7y+3sEH5wsV86h7E9JTV95m+k/dZmMUIJLsLjfbPa4dyHPr7rCTTsfHOb7TTJpa8a5cysljMcfV6jU09mg4NvH9RnUwf+pelbSO9rqvfDMyPEIGaOshrb7H7YudnrgR0vbwHRFlJ1mEkWojIQQ8G6I6HTt4AV4bpcRL1TV/LOseltyzOU7KRmhjAS5XqrgIdY9TkXEx958TltCCtIMKP16Zm0FnXOruuVpKpp/AS8zftNJWZfLH7kumCnXu6LPMotp6R9DDlqGIBzdtZMS8pM+ja6U8W0FHPdFM1gNceulWUxZQKANu7C3FZdXfb2wygNpLDyEx3dki4mCOA/liTe+8cD6wYt64C9J1+vF3CqfUdsmLGMstFsYW5t7PCFl1u0EnXPzClOja6LbxKRnIxIwKf0beHV/lzoSrQLZl6MoHHjDAHugjoXr44Sackknf0N1DTxWg16h2PGDDHiTWVnOmeGyxsxSVTw1YzwFaA6U7bkS5gPwrVC+7dDBNJd5QoEF17+GxFcRwXlXeQiqU4tMZAJaOsQMWtzFzCOMU6kgJM18bJxkIsxkcdAx0ARzGl5bOg7deqgqZHs8s7dDlA11t1jg0EMf4FcHHpP6zs0dyga/cmAH0YMEpl4oRyCdIy3riFFL7hVGlyH2eEJDyIGsjddnZlkdrtMp1jviLNQUBRrL268UgWx0+3SkZ7+KtRKSrC9Ea1QaMhUTduuGCheskw7z5Bzr19wezVsRp1H4G+xGh7X8i1GbGJKhF+1IvsAslb731j+nFvBRsXVvcH6KQkSdBwiO4l04DFBgVgf9fQPV97/+pUGlWc6Shr5OV7fN3o7fZPfx3jfUOo52+vHvj+9363qPuvVJYdyHFWXeYbT2GvP79WZk7fqrwD6pB3T2fcxQGXvYGi6YQuvlfnpD5Acxpd6466fC0z0b3yvt1KUnbaJB/SNJHSB2ebQ/JoMDQCt3WwKtcHeivvn16PiL1cmOdXbNEzarJ8YlkRQNMe/lDF6v4A3U18kqyZQV0TF3S0Y8exXQKAe1cNqmbH/Ml00qNGmUCfP0i6sVuhkXS4lt01bS4FuhrBwHkUuhclIUKcR+o9A8fN+w+rxMKVAt01Yq8ehemqNwWz6O7kW/H2gJ52xIXo38uI/j/B9CajUx+hzuFc5XR6fz1skd538zUsj/nAdPI3k6dNfcYBmFzfXQQ+psns7vF6X3Rvb0NW7cd5SnCujegzeR+3k71N3e0dTAS6IVD3sT40FJjy5fjzN6Hm5WL6ftLGNu1kpX0HsxyjJ+U8KcjwnfLwcSZQjdG40kzHSYs8GwODoXvwyQ5VM2cpJRZT9o0zZfCm7ARwPHwOv26ppaY6dZ8yvW1BrpChtTJdO2kSDsMKIJwGZYnoPf7dF+MuHp3UkllU3kGx+wJtgI5pUF8b2ciBMcogf3RKOyZ6pS7d7tNn5RlUpzoAZtR3nsD7T8VkdL/CT/B21k4yhV8GuiBT5218dG9rpTc3OT9Xom93ZVcF5gPoTm3r82vEh52w3asbT1rG+LRefVb79sUG7tQG+jZxIHjUMx3VRVNm7Un6tCmjAP14+N9eGAd1uPQR6IFO3TIcZQvgN20xdSLnj+lGoft1d4Ln/gSDabW/GnTQHNEWJXClxJhZLoTRVzr8+RSoREuDUZQQ9zHo6nly4fquRX21pM0ZKLxXdh1+difpls/PAL8M6o10UAO3yzOcC/R406E50ndGMTl00+E1mPnx7hEAkN3DxQedlG8P205dunRc/c404UlO972pBvdF6P2EmUUx7qLg7cOL05jnV39fyKPvgs4Sngq6huPFcvqTv/PEcnR5E1iMo8MD+A3/AIUwH4PuLOocqI6UQ+A5feWGyLWHV5gmZa3vcYXUfcL0h2wKuwIsYy48Bx0CYjk9RTmavcwoDe3bV2dhTWmmawEke6bOVlsnpQJjoHCLaw4Xok+dD0xjdaikBLuQd1J2zwh0Nmlmp7d1CgWNvX9mA9nbj6+N4ts1LV8dVi+F+YLpuj0YvNRjP0VcdK4/bQAjSvju4Bq5959PteiR+4XyPo/k2NK3NqpzCvisK1ouH3cazu2ARQ2H1ko6xTz6FHR9lBRafjdpXFPXp6+vu7efDnYrtVSRuk9BJy0HTKDjPBgM0XcNuXnSAo4VxQF0eHilB8C0Mvy26tdrB31H3ylUP8Gl8er/b+9q09vEgTDzzAVGD3uCtPk/1On/bZxcwBvf/yqLABsMEkjDCEvZddqUxB9Feuedb0kJcrXsvIbG/L40cICOy0a9m62ctNOlHZ+VJChSAuv6/UJYGacr7lsv7g/FS77JA3QToBVdqI+pp2FzpdpeQDVe15VXACQC70iAJwzCbTnUbcv7i9Prl+Ex25490R9BjzeV/btgnPWlaYOZpXusPMgk/nh3bSFUDdq/BFBZ5b6a4uCsQX+0lhiO+pRu09TTQxrKPt2Rvr2opyJh0sLu9iQaWYjXvg/6+4aO1sD01/Us8TJyAZ1Edz6sPtkoeHZP2zIk3JT77Q2SHIXKnoJCgcEHz53w9R8icrotmWK+xvR4V85SmEZVP732WGESuzSBiPGjjmWNytpdvLtyuVXuVUm+uxP0mFmBEXQazHf31divrs58u6ahO8z+geb2wqb7neTGQdlZmyxS8xxPMXkGmqYfifXcz0Gb9XA+mM9Aj/eQaP6u3n+nh2saHfzp0+OFBtXlCj9Iyq2QobXh9U3C7duobj13a9hLIvqc6egrMaGPT0ZjU/Z4m35ciuZOdKurGh4M0zAP8NmtboClGKHyiBMynSrBIQ9m94SK5J4qT5iQqLIy3VicB+P26/onPDKf+hh5gV41TcjN40RPPYHpDx48VJSgd44dw+bHFfzw1/WHrEsTsgIdQQDY7hmXzsGRxTSezwxR/fll66qCiMBkBbqk5tKBzruoJLx7mCX74pX6jgCuVe4XPlCzJQM9cBX4Ij9TPYnrRGLbsiPE699Kv64/7Gm6tDFHnD/oFEsAUFGzUgVPAa4cp5GLt+sPAxunMlJ+QboDdBIVXWg2YA6cOVQQ/uo5D1tbCy6qUVZEX4BuZAbqjnR3fEOfv8L+p/47Lr5u4sE7qU5hadg4H4NXZda6EvWnL/06ST5DliZ9Cbqw6MIYaddw7ELoL8RDCHE+2ZlkFev91pK/X/+M69SWqXn7rfalCt1OaUZMZ7+RY1fQxuPL0MUZXHw+7xT/YQGh2FqjAHPzyxp0WJecvrgwtAghZUP0JehGxPRBv49tRN0ZPkOR4v7b4WAfHotek+wu7EO99iSP9mRlfM4hQevEtf/jqorpFiVDX2qyNaexNdp8A/Vu4dKob5ukCj4eevbyF+rrF9uG90fZauZiMpOZJg/fXcJ0TBS07ZkMUN5PkHHNEzf16xd3EfqjtNF6rQ9y0e7BoPMGRzVA30WBo4puLdD164X7PWNIoEAgR9Ap3sV5PtUTHLrN6NPtn5e/wdy75QpLwW6CHhqBgQ7Rnk51fNDPsLBpltrYYn7uWv1IVGCDLEE3MunVQH3vWAJlFdesrn9JGrcwW57b/qinjfEw0DEM9G3Bp6QsIKNbRZsGXwPPyVjM5wv5sTCiu0Anoc6qBucG+kbI/od6+i/2V/0XNN2m+N335tYzv28wrtANZQjPVhrfet0t5q7uIiyJ6B7QOR70bsKbCmnge9NedVmo8d92spBWJ2svDyICdmdxrJW+mbq437AV0I7ndlFLxFkIGRLdBbqJkF3emZ8BdSJAYJaBqtvCuwXJ74pneoNgD0v+7PudafWTeWXyTN6gR7snKjGTAhNILnK9MEzXaoy5WALz9no972u3h3xBpyCliDPUdXKhx6L+yEy4tVhSF47BAPjweS3ml/OxOyA/i+l4MNUVqhFh0ueMNaxH0jz6cT3u7W1ZzEVDxNyI7gTdyBiqlAg1R3Gda+eKe5d8gIFf1wtHDRGzJboAdEyZidWiA0Vp9231U7eYN3JVhlkR3Q26JNwFtW41lXF1Oh7jyegeW+u2/2TCDKQ5PegcMluYJei23OopuGJY9xyMTQ+t2/6jMZmMKxXoRjYmygt1A/OCd3QBqBpWI1/OojpqUaCTcExxVg59VlVRD+6IKPpuJzCn1+tXE79vbsba3cN0SUFh4r/j8q3ohNvzO835kWZSYPDg3q7Xn82WxuCiiK6o3oP8dw6aLcXxhet3nql2m2JvTu/Xr0WkxusfwN7N10phOocfXeoHnZ9q/mR2HYCgfrc0B7FGLwx0XO6fGtdJgdWRpzjpN9XYrS5ap/1yBtqXacT8MPeAbiQ+i15+Rt8AUhXb0GZazX49N6R0cLcpCHSOOBwD+lAd95JCKxfbhmxmTOVDoCzDkI6xmp1px7HtnKty94Iuyj5tTCvmQA1adHFwvwTnjilQz/KfXUnNE59zHPx5Ye4DHaQ6TMGLS6wP58TuMe0SeF0OD8xbC/llcTyyXM0TlAG6EfmrIp+JD/Z2YRaZdeDeEDd0+ni1LF9CXIt9+Nww94JOMaqZ9Tw5Tkj1eqHf75gPDjtakl9/nmwGF5Ypuq2p8DzVmDJBV8zPPFvBT7Vua8D7RrnWkLccf2kRv3ycWvBJccEMmFKYHgzePH1KoYTmLXOvPlVvZwar3qkz4LYa0zv1QHh6e3+1iLckt0PfUadZjqkB891AD+yf4Uc7Cr4xAAADhElEQVS4g0y79lh/t7C+vHycT6fTTVnj6fT28W4Z3j71cap6V85XlPXpb97KVJYCOq0zm1f1O8d57gf5cqePjs6Ox8WKgrF+XFVVKwVZkASiVA7oRmqHyT8b+GyrXpv69PHx8vJyuVior6/t5UeLd9M0dje10XvTag2A/Fx3CdPXMTSaoXoSxdhtPF8hthCf7ACMdea2bfhtCDVFWff8WB7KdIwz6mIHHo9AffI/pT8TBJqqLNBpr35XeOgThRqx8sF1GqB7AFAU6GKj/og6RpsITpqWU7I8gV2jWWIeBzoH6XfNnUD0mT6NLHjn3eGmXJjCbLolLC+OOwrqPtDMZiVmemKbXhzosmF6U7Es4U4Cpid5sHuMpjjQhaccgubMqlOF6v+JLgJ91Y21VCdC2hIRXmsf5XSgHwM354z5KugkFu8hAkYX3PhcsoS547himjDUnaMSQRf2PhnNbTu1QadGl9JYnnLfAD2sOwwDEjQc7gwl9oWIE2F8XMNXWtCFfO3fyQoJEH2+xJcUOG4QnGuP1B6mB9AXwj1/3HwmOegczOAS9xSKB93IyemaKFFHvLI31MUV6JNbjJBVXqNAzjxXBZ11qi6YljF7BDD8UZtyQSeZSQajmO6ExOp9fx6uKCduG3SDYpxIaxqVqQ5KDuaqkjJQMtNhOkMcPOoY0LfkqoSMHJfjxAUwnapEVOc14DGhJ7fXKy88RD8WdJZGvso23V8V4DBlxEff8uGgm61ZQH9JlKKm1t9Cc7B+5/3c/4ags4p+f5auTOW7FaPbA0Dfod+j38uH8IZ17sr/gm8AOm37PI4aDI1UX6um4hOU5Q5o+Ruo9hDQ1+jKW0p5jiqK1KayJ0cK8Tl7jgMpguf7QD8oK6c84gf3nePdlaKDtUDQzY7hk5BF6dMzrAEwlqncQ0DfQXXKkOk0PzFKMzVjzH8c9HCqH5uIBW12T24Uvgvokf0QGEJ19s0iH9A7o9wlVxzP5aBjNNUx/BP48TVGW8HHuhUY9HqAbwS6WL+DcWzGi5KPORBzqYkvB/Ig0O/9zLw1RejYG1O+Oz6nKbMZx44yHBOvYemYh4BuKunm2R2xYO8qVu3phEZvWS2OQ/1eTB80Im2ykl1Gnci/Y0eYKlVn+n0jIY6D13/uegOBTKdiQKfbJpoRE2VPQLkpU/BWYINEQNumE8QcwhMkmFSbkh7/Ah6cmAsR+12jAAAAAElFTkSuQmCC'
                },
                dropZone: true,
                tutorial: {
                    display: false,
                    alreadyWatched: false
                }
            },
            buttons: {
                createContent: {
                    display: true,
                    disabled: false
                },
                deleteAll: {
                    display: true
                },
                continue: false,
            }
        };
    };

    /**
     * Gestion des actions liées à la première visite de la page.
     */
    $scope.manageFirstVisitActions = () => {
        let cookie = "thaleia-cannelle-createContent-alreadyViewedPage-" + URL.toString();
        $scope.firstVisit = getCookie(cookie) === null;

        // Si l'interface de l'IHM est en anglais on change la source de la vidéo pour la version anglaise.
        if ($("#thaleia-xl-content").attr('lang') === 'en') {
            $scope.interface.workspaces.introVideo.url = 'https://www.solunea.fr/wp-content/uploads/Create-your-e-learning-modules-with-Thaleia-XL.mp4';
            $scope.interface.workspaces.introVideo.vtt = 'https://www.solunea.fr/wp-content/uploads/Create-your-e-learning-modules-with-ThaleiaXL.vtt';
            $scope.interface.workspaces.introVideo.vtt_fr = 'https://www.solunea.fr/wp-content/uploads/Creer-votre-module-de-formation-avec-ThaleiaXL.vtt';
        }

        if ($scope.firstVisit) {
            // Affichage de la vidéo de présentation lors de la première visite de la page.
            $scope.switchIntroVideo($scope.firstVisit);

            //Toutes les actions de première visite de la page ont été faites, on ajoute le cookie de première visite.
            document.cookie = cookie + "=true";
        }
    };

    /**
     * Initialise l'API Thaleia.
     * @detail Récupère le token de connexion.
     * @return {Promise<void>}
     */
    $scope.initThaleiaAPI = async () => {
        try {
            return new Promise(function (resolve, reject) {
                API.getTokenFromThalieaAPI()
                    .then(_ => resolve(),
                        rejected => $scope.manageError(
                            $scope.localisation.getLocalisedString("error_initAPI_getToken"),
                            rejected
                        )
                    )
                    .catch(function (e) {
                        $scope.manageError(
                            $scope.localisation.getLocalisedString("error_initAPI"),
                            e
                        );
                    });
            });
        } catch (e) {
            $scope.manageError(
                $scope.localisation.getLocalisedString("error_initAPI")
                , e
            );
        }
    };

    /**
     * Vérification de la connexion à Thaleia.
     * @detail Si la requête getToken ne passe pas c'est probablement que la session wicket est HS
     *      Du coup on recharge la page courante pour en créer une nouvelle. Si l'utilisateur est déconnecté
     *      il sera envoyé dans la mire de login Thaleia.
     * @return {Promise<unknown>}
     */
    $scope.checkConnexion = async () => {
        try {
            return new Promise(function (resolve, reject) {
                API.getTokenFromThalieaAPI()
                    .then(_ => resolve(),
                        rejected => location.reload()
                    )
                    .catch(function (e) {
                        location.reload();
                    });
            });
        } catch (e) {
            location.reload();
        }
    };

    /**
     * Initialisation du gestionnaire de fichiers.
     * @detail Cette fonction gère la reprise de session (récupération du tempdir précédent et de son contenu).
     * @detail Cette fonction DOIT ETRE UTILISEE APRES l'initialisation de l'API Thaleia.
     */
    $scope.initFilesManager = () => {
        try {
            $scope.filesManager = new FilesManager();
            getWorkingTempDir()     // Récupération du tempdir courant.
                .then(tempdir => {
                    $scope.filesManager.setTempdir(tempdir);
                    getFilesInTempDir($scope.filesManager.getTempdir())
                        .then(APIFiles => {
                            angular.forEach(APIFiles, function (APIFile, key) {
                                // On crée un objet myFile qui sera plus pratique à manipuler que
                                // l'objet générique retourné par l'API.
                                let myFile = new MyFile(APIFile);
                                $scope.filesManager.addFile(myFile);
                            });
                            $scope.$apply();
                        });
                });
        } catch (e) {
            $scope.manageError(
                $scope.localisation.getLocalisedString("error_initAPI_getToken"), e);
        }
    };


    //------------------------------ Gestion de l'espace de travail

    /**
     * Active ou désactive la vidéo d'introduction et les éléments associés.
     * @param {Boolean} visibility Affichage ou non de la vidéo.
     */
    $scope.switchIntroVideo = function (visibility) {
        $scope.interface.workspaces.introVideo.visibility = visibility;
        $scope.interface.workspaces.dropZone = !visibility;     // Caché pendant la vidéo.
        $scope.interface.buttons.createContent.display = !visibility;        // Caché pendant la vidéo.
        $scope.interface.buttons.deleteAll.display = !visibility;        // Caché pendant la vidéo.
        $scope.interface.buttons.continue = visibility;

        // Lors de la première visite de la page, on affiche le tutoriel après la vidéo.
        if (!visibility && $scope.firstVisit && !$scope.interface.workspaces.tutorial.alreadyWatched) {
            $("#tutorial,#content").addClass("show");
            $('#sidebar,#content').toggleClass('shrinked');
            $('[data-toggle="tooltip"]').tooltip('enable');
        }

        $scope.manageOverlay(visibility);
    };

    /**
     * Active ou désactive l'overlay.
     * @detail L'overlay permet d'assombrir l'espace de travail.
     * @param {Boolean} activate
     */
    $scope.manageOverlay = function (activate) {
        if (activate === true) {
            $(".overlay").addClass("active");
        } else {
            $(".overlay").removeClass("active");
        }
    };

    //------------------------------ Ajout de fichier

    $(document).on('change', '#file_upload', function (event) {
        if (this.files !== undefined && this.files.length > 0) {
            // Si le seul fichier importé est un zip, il est traité comme une archive de création de contenu cannelle
            if (this.files.length === 1
                && checkFileExtension(this.files[0].name, 'zip')
                && $scope.filesManager.countFiles() === 0) {
                $scope.createContentFromZipFile(this.files[0]);
            } else {
                // La liste de fichiers est convertie en Array de façon à être plus facilement manipulée ensuite.
                $scope.addFiles(Array.from(this.files));
            }
            // L'élément étant traité hors du cycle standard d'AngularJS il faut forcer la mise à jour.
            $scope.$apply();
        }
    });

    /**
     * Ajout récursif d'une liste de fichiers.
     * @detail Tant que la liste n'est pas vide, la fonction s'auto-appellera afin de traiter un fichier
     *          toutes les 1200ms. Celà permet d'éviter de bloquer le serveur cayenne avec des écritures concurrentes.
     * @param {[{File},{File,...}]} files Liste des fichier à ajouter.
     */
    $scope.addFiles = (files) => {
        $scope.checkConnexion();
        try {
            if (files.length > 0) {
                // On désactive le test de connexion car elle a déjà été testée au début du traitement.
                $scope.addFile(files.shift(), false);
                if (files.length > 0) {
                    setTimeout(function () {
                        $scope.addFiles(files);
                    }, 200);
                }
            }
        } catch (e) {
            $scope.manageError($scope.localisation.getLocalisedString("error_addFiles"), e);
        }
    };

    /**
     * Ajout d'un fichier.
     * @detail Le processus d'ajout suit les étapes suivantes :
     *          - Instanciation d'un objet MyFile avec ID temporaire correspondant au fichier.
     *          - Ajout de l'objet MyFile au FileManager.
     *          - Envoi du fichier à l'API Thaleia.
     *          - Si l'envoi réussi : Mise à jour de l'ID de l'objet MyFile du FileManager avec l'ID retourné par l'API Thaleia.
     * @param {File} file Fichier à ajouter.
     * @param {Boolean} checkConnexion Est-ce qu'il faut tester la connexion pour l'envoi de ce fichier. Cette option
     *          est utilisée pour ne pas avoir à tester pour chaque fichier lors d'un ajout en masse de fichiers.
     *          L'ajout en masse sature le serveur qui ne répond pas toujours au test de connexion ce qui force un
     *          refresh de la page et perd les actions en cours. Bref c'est pas top, du coup on désactive la vérification
     *          pour un lot de fichier. De toute façon, la connexion est testée au début du traitement du lot de fichiers.
     */
    $scope.addFile = (file, checkConnexion = true) => {
        if (checkConnexion === true) {
            $scope.checkConnexion();
        }
        let errorMessage = $scope.localisation.getLocalisedString("error_addfile")
            .replace("${file}", file.toString);
        try {
            let myFile = new MyFile(file),
                url = `${$scope.filesManager.getTempdir()}/files`;

            myFile.setStatus("uploading");
            // Ajout du fichier au gestionnaire
            $scope.filesManager.addFile(myFile);
            $scope.filesManager.getFileById(myFile.getId()).setProgressbar(true);
            $scope.$apply();

            // Envoyer le fichier à l'API
            API.addFile(url, file)
                .then(resolved => {
                    // Si réussite : mise à jour de l'ID du fichier.
                    let fileId = resolved.split("/").pop();
                    $scope.filesManager.updateFileIdById(myFile.getId(), fileId);
                    $scope.filesManager.updateFileStatusById(myFile.getId(), "uploaded");
                    if (DEBUG) console.log(`Le fichier "${file.name}" a été ajouté avec l'ID "${fileId}"`);
                    $scope.$apply();
                }, rejected => {
                    // Si échec : notification + indicateur visuel sur le fichier
                    $scope.filesManager.setFileOnErrorByName(file.name);
                    console.error(rejected);
                    $scope.$apply();
                })
                .catch((e) => {
                    $scope.filesManager.setFileOnErrorByName(file.name);
                    $scope.manageError(errorMessage, null, e);
                })
                .finally(() => {
                    $scope.filesManager.getFileByName(myFile.getName()).setProgressbar(false);
                    $scope.$apply();
                });
        } catch (e) {
            $scope.filesManager.setFileOnErrorByName(file.name);
            $scope.manageError(errorMessage, null, e);
        }

    };

    //------------------------------ Suppression de fichier

    /**
     * Suppression d'un fichier.
     * @param {MyFile} file
     */
    $scope.deleteFile = (file) => {
        if (DEBUG) console.log(`Demande de suppression du fichier "${file.getName()}".`);
        $scope.checkConnexion();
        let errorMsg = $scope.localisation.getLocalisedString("error_deleteFile")
            .replace("${file}", file.getName());
        try {
            // Récupération de la liste des fichiers du répertoire temporaire
            getFilesInTempDir($scope.filesManager.getTempdir())
                .then(APIFiles => {
                    let fileExist = false;
                    for (let i = 0; i < APIFiles.length; i++) {
                        if (APIFiles[i].name === file.getName()) {
                            fileExist = true;
                        }
                    }

                    // Si le fichier est dans la liste on demande sa suppression
                    if (fileExist) {
                        API.deleteFileInTempdir($scope.filesManager.getTempdir(), file.getId())
                            .then(resolved => {
                                if (DEBUG) console.log(`Le fichier "${file.getName()}" a été supprimé dans le répertoire temporaire.`);
                                if (DEBUG) console.log(`Suppression du fichier "${file.getName()}" dans le scope.`);
                                $scope.filesManager.deleteFileById(file.getId());
                                $scope.$apply();
                                if (DEBUG) console.log(`Le fichier "${file.getName()}" a été supprimé.`);
                            }, rejected => {
                                $scope.manageError(errorMsg, rejected);
                            })
                            .catch(function (e) {
                                $scope.manageError(errorMsg, e);
                            });
                    }
                    // Sinon on le supprime juste de la liste du $scope
                    else {
                        if (DEBUG) console.log(`Suppression du fichier "${file.getName()}" dans le scope.`);
                        $scope.filesManager.deleteFileById(file.getId());
                        $scope.$apply();
                        if (DEBUG) console.log(`Le fichier "${file.getName()}" a été supprimé.`);
                    }
                });
        } catch (e) {
            $scope.manageError(errorMsg, e);
        }
    };

    /**
     * Suppression de tous les fichiers.
     * @detail Procédure détaillées :
     *          - Appel à l'API Thaleia pour supprimer le tempdir courant.
     *              - Appel à l'API Thaleia pour créer un nouveau tempdir.
     *                  - Enregistrement du nouveau tempdir dans le FileManager.
     *                  - Enregistrement du nouveau tempdir dans les cookies.
     */
    $scope.deleteAllFiles = () => {
        $scope.checkConnexion();
        let errorMsg = $scope.localisation.getLocalisedString("error_deleteUploadedFiles");
        try {
            // Affichage du Loader sans message.
            $scope.notifier.displayNotification('loader', '');
            API.deleteTempdir($scope.filesManager.getTempdir())
                .then(resolved => {
                    $scope.filesManager.reset();
                    API.createTempdir()
                        .then(resolved => {
                            // Enregistrement du nouveau tempdir dans le FileManager
                            $scope.filesManager.setTempdir(resolved);
                            // Enregistrement du nouveau tempdir dans les cookies pour les reprises de session ultérieures.
                            document.cookie = `thaleia-cannelle-createContent-workingTempdir=${$scope.filesManager.getTempdir()}`;
                            $scope.$apply();
                        }, rejected => {
                            $scope.manageError(errorMsg, rejected);
                        });
                }, rejected => {
                    $scope.manageError(errorMsg, rejected);
                })
                .finally(() => $scope.notifier.clear())
                .catch(e => {
                    $scope.manageError(errorMsg, e);
                });
        } catch (e) {
            $scope.manageError(errorMsg, e);
        }
    };

    //------------------------------ Création du contenu pédagogique

    /**
     * Création d'un contenu pédagogique à partir des fichiers uploadés.
     */
    $scope.createContent = function () {
        $scope.checkConnexion();
        let errorMsg = $scope.localisation.getLocalisedString("error_createContent");
        try {
            if ($scope.filesManager.getAtLeastOneExcelFileInList() === false) {
                // TODO Affichage d'une infobulle "Il manque le fichier excel".
                $scope.notifier.displayNotification('error',
                    $scope.localisation.getLocalisedString("warning_excelFileIsMissing_title"),
                    [$scope.localisation.getLocalisedString("warning_excelFileIsMissing_text")]
                );
            } else {
                if (DEBUG) console.group("Demande de création d'un contenu pédagogique à partir des fichiers uploadés.");
                if (DEBUG) console.log("Affichage du loader.");
                $scope.notifier.displayNotification('loader', $scope.localisation.getLocalisedString('creationInProgress'));

                let type = "cannelle_import",
                    locale = $scope.localisation.getLocale(),
                    instance = getThaleiaInstanceUrl(),
                    fileUrl = $scope.filesManager.getTempdir();
                if (DEBUG) console.log("Informations de la requête API :", {type, locale, instance, fileUrl});

                API.transformFromUrl(instance, fileUrl, type, locale)
                    .then(resolved => {
                        if (DEBUG) console.log(resolved);
                        $scope.filesManager.setContentVersion(resolved.content_version_id);
                        $scope.notifier.displayNotification('moduleIsReady', $scope.localisation.getLocalisedString('moduleIsReady'));
                        $scope.displayButtons(['createContent', 'deleteAll'], false);
                        $scope.$apply();
                    }, rejected => {
                        $scope.manageError(errorMsg, $scope.splitErrorDetails(rejected.description));
                    })
                    .catch(function (e) {
                        $scope.manageError(errorMsg, null, e);
                    });
            }
        } catch (e) {
            $scope.manageError(errorMsg, null, e);
        } finally {
            if (DEBUG) console.groupEnd();
        }
    };

    /**
     * Ajout d'un fichier zip en tant que contenu cannelle.
     * @param {File} file Fichier zip à ajouter.
     */
    $scope.createContentFromZipFile = function (file = null) {
        $scope.checkConnexion();
        let msgError = $scope.localisation.getLocalisedString("error_createContentFromZip");
        try {
            $scope.notifier.displayNotification('loader', $scope.localisation.getLocalisedString('creationInProgress'));

            let instance = getThaleiaInstanceUrl(),
                type = "cannelle_import",
                locale = $scope.localisation.getLocale();

            API.transformFromZipFile(instance, type, locale, file)
                .then(resolved => {
                        $scope.filesManager.setContentVersion(resolved.content_version_id);
                        $scope.notifier.displayNotification('moduleIsReady',
                            $scope.localisation.getLocalisedString('moduleIsReady'),
                            null,
                            ["moduleIsReady.modifyBtn"]);
                        $scope.displayButtons(['createContent', 'deleteAll'], false);
                        $scope.$apply();
                    },
                    rejected => {
                        $scope.manageError(msgError, $scope.splitErrorDetails(rejected.description));
                    })
                .catch(function (e) {
                    $scope.manageError(msgError, e);
                });

        } catch (e) {
            $scope.manageError(msgError, e);
        }
    };

    /**
     * Prévisualisation du contenu pédagogique.
     */
    $scope.preview = () => {
        $scope.checkConnexion();
        let errorMsg = $scope.localisation.getLocalisedString("error_previewContent");
        try {
            // Désactivation du bouton pour éviter que l'utilisateur ne le mitraille en attendant la réponse
            // du serveur et la redirection. Le bouton sera réactivé après traitement.
            $("#notifyer-btn-preview").addClass("disabled");
            $("#iconPreviewLoader").removeClass("d-none");

            API.preview(getThaleiaInstanceUrl(), $scope.filesManager.getContentVersion())
                .then(resolved => {
                    window.open(resolved.preview_url, '_blank');
                }, rejected => {
                    $scope.manageError(errorMsg, null, rejected);
                })
                .catch(function (e) {
                    $scope.manageError(errorMsg, null, e);
                })
                .finally(function () {
                    // Réactivation du bouton "Prévisualiser" après traitement.
                    $("#notifyer-btn-preview").removeClass("disabled");
                    $("#iconPreviewLoader").addClass("d-none");
                });
        } catch (e) {
            $("#notifyer-btn-preview").removeClass("disabled");
            $("#iconPreviewLoader").addClass("d-none");
            $scope.manageError(errorMsg, null, e);
        }
    };

    //------------------------------ Nouvelle création

    /**
     * Prépare l'espace de travail pour la création d'un nouveau contenu.
     * @detail Création d'un nouveau tempdir, réinitialisation du FilesManager et assignation du nouveau tempdir.
     */
    $scope.prepareWorkspaceFornewCreation = function () {
        $scope.checkConnexion();
        let errorMsg = $scope.localisation.getLocalisedString("error_prepareWorkspaceForNewCreation");
        try {
            $scope.notifier.clear();
            // Création du nouveau tempdir
            API.createTempdir()
                .then(resolved => {
                    // Purge du filesManager
                    $scope.filesManager.reset();
                    // Assignation nouveau tempdir
                    $scope.filesManager.setTempdir(resolved);

                    // Réaffichage des boutons.
                    $scope.displayButtons(['createContent', 'deleteAll'], true);

                    $scope.$apply();
                }, rejected => {
                    $scope.manageError(errorMsg, $scope.splitErrorDetails(rejected.description));
                })
                .catch(e => {
                    $scope.manageError(errorMsg, null, e);
                });
        } catch (e) {
            $scope.manageError(errorMsg, null, e);
        }
    };

    //------------------------------ Gestion des erreurs

    /**
     * Log une erreur dans la console et affiche la notification à l'utilisateur.
     * @param {String} msg Descriptif de l'erreur.
     * @param {[]} detail Liste des logs détaillées de l'erreur.
     * @param {Error} error Détail de l'erreur.
     */
    $scope.manageError = (msg, detail = null, error = null) => {
        $scope.notifier.displayNotification('error', msg, detail);
        $scope.$apply();
    };

    /**
     * Découpe et retourne la description des erreurs rencontrées par l'API Thaleia.
     * @param {String} rawString Description des erreurs.
     * @return {[{String}, {String}, ...]}
     */
    $scope.splitErrorDetails = (rawString) => {
        let result = [],
            splitted = rawString.split('\n');

        angular.forEach(splitted, function (value, key) {
            if (value.trim().length > 0) {
                result.push(value);
            }
        });

        return result;
    };

    //------------------------------ Gestion de l'interface.

    /**
     * Gestion de l'affichage des boutons de l'interface.
     * @param {[String, String]} buttons Boutons à gérer.
     * @param {Boolean} visibility Affichage des boutons ?
     */
    $scope.displayButtons = function (buttons, visibility) {
        angular.forEach(buttons, function (button, key) {
            $scope.interface.buttons[button].display = visibility;
        });
    };

    $scope.init();
}]);
