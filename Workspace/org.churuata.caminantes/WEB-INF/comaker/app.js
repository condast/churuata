//HAMBURGER
const hamburger = document.querySelector('.header .nav-bar .nav-list .hamburger');
const mobile_menu = document.querySelector('.header .nav-bar .nav-list ul');
const menu_item = document.querySelectorAll('.header .nav-bar .nav-list ul li a');
const header = document.querySelector('.header.container');
var iframe = document.getElementById("myIframe");

hamburger.addEventListener('click', () => {
	hamburger.classList.toggle('active');
	mobile_menu.classList.toggle('active');
});

document.addEventListener('scroll', () => {
	var scroll_position = window.scrollY;
	if (scroll_position > 250) {
		header.style.backgroundColor = '#0066cc';
	} else {
		header.style.backgroundColor = 'transparent';
	}
});

menu_item.forEach((item) => {
	item.addEventListener('click', () => {
		hamburger.classList.toggle('active');
		mobile_menu.classList.toggle('active');
	});
});

//MAPBOX
mapboxgl.accessToken = 'pk.eyJ1IjoiYnVyaGFuMCIsImEiOiJjbDNhOG9pMTUwMzkxM2NvaWowN2dtcmpmIn0.zc9H-ZUCIJj9EJQBATi7Yg';
    const map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/streets-v11',
    center: [6.152375420720938, 52.25993386352351],
    zoom: 9
    });
    getMapinfo();

    function getMapinfo(){

        return fetch(
            "http://www.condast.com:8080/churuatas/organisation/find-all",
            {   method: 'GET',
                headers: new Headers(
                   {"Content-Type": "text/plain"}
                )
             }
           ).then((response) => { 
            return response.text().then((data) => { 
                //console.log(data)
                return dynamicOrganisationLoad(data); })
                
            .catch(err => console.log(err))
            
            })}

function dynamicOrganisationLoad(resultValue){
    var parsedResult = JSON.parse(resultValue);
    //console.log(parsedResult);
    //Loops through the JSON.
    for (let i = 0; i < parsedResult.length; i++){
    //console.log(parsedResult[i]['name'] );
    map.on('load', () => {
        //OrganisationId.toString, map.addSource takes string only
        map.addSource(parsedResult[i]["organisationId"].toString(), {
        type: 'geojson',
        data: {
        type: 'FeatureCollection',
        features: [
        {
        type: 'Feature',
        properties: {
        description:
        parsedResult[i]["location"]['id'] + '<br>'+ parsedResult[i]['description'] + '<br>' + 
        parsedResult[i]['contact']['firstName'] +' ' + parsedResult[i]['contact']['surname'] +'<br>' + parsedResult[i]['type'] 
        + '<br><a href="showOrganisation.html?'+parsedResult[i]["organisationId"]+'">More information</a>',
        icon: 'bank-11'
        },
        geometry: {
        type: 'Point',
        coordinates: [parsedResult[i]["location"]["lo"], parsedResult[i]["location"]["la"]]
        }
        }
        ]
        }
        });
        map.addLayer({
        id: parsedResult[i]["organisationId"].toString(),
        type: 'symbol',
        source: parsedResult[i]["organisationId"].toString(),
        layout: {
        'icon-image': '{icon}',
        "icon-size": 4,
        'icon-allow-overlap': true
        }
        });

        map.on('click', parsedResult[i]["organisationId"].toString(), (e) => {
        const coordinates = e.features[0].geometry.coordinates.slice();
        const description = e.features[0].properties.description;
         
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
        coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }
         
        new mapboxgl.Popup()
        .setLngLat(coordinates)
        .setHTML(description)
        .addTo(map);
        });
       
        map.on('mouseenter', parsedResult[i]["organisationId"].toString(), () => {
        map.getCanvas().style.cursor = 'pointer';
        });
         
        map.on('mouseleave', parsedResult[i]["organisationId"].toString(), () => {
        map.getCanvas().style.cursor = '';
        });
        });
}
}
function showOrganisationDetails(resultValue) {
    var currURL = window.location.href;
    var organisationId = currURL.slice(currURL.length - 3, currURL.length);
    console.log(organisationId);
    var parsedResults = JSON.parse(resultValue);
    var parsedResult;
    for (let i = 0; i < parsedResults.length; i++){
        if (parsedResults[i]["organisationId"] == organisationId){
            parsedResult = parsedResults[i];
            
        }
    }
        console.log(parsedResult);
        var organisationNameField = document.getElementById("organisationName");
        var organisationContactField = document.getElementById("organisationContact");
        var organisationDescriptionField = document.getElementById("organisationDescription");
        var organisationVerifiedField = document.getElementById("organisationVerified");
        var organisationMailField = document.getElementById("organisationMail");
        var organisationWebsiteField = document.getElementById("organisationWebsite");

        var organisationName = parsedResult['name'];
        var organisationContact = parsedResult['contact']['firstName'] +' '+ parsedResult['contact']['surname'];
        var organisationDescription = parsedResult['description'];
        var organisationVerified = parsedResult['verified'].toString();
        var organisationMail = parsedResult['contact']['contacts'][0]['value'];
        var organisationWebsite = parsedResult['website'];

        organisationNameField.innerHTML = organisationName;
        organisationContactField.innerHTML = organisationContact;
        organisationDescriptionField.innerHTML = organisationDescription;
        organisationMailField.innerHTML = organisationMail;
        organisationWebsiteField.innerHTML = organisationWebsite;

        if (organisationVerified == "true") {
            organisationVerifiedField.innerHTML = "This organisation is verified.";
        }
        if (organisationVerified == "false") {
            organisationVerifiedField.innerHTML = "This organisation hasn't been verified yet!";
        }

        var services = document.getElementById('services');
        for (let i = 0; i < parsedResult['services'].length; i++){
        services.innerHTML += "<ul> " + parsedResult['services'][i]['contribution'] + " " + parsedResult['services'][i]['serviceType'] + "</ul> ";
        }
        //console.log(parsedResult['services'].length);

        if(parsedResult['services'].length == 0){
            services.innerHTML = "No services!";
            
        }
    
    }

function getUserInfo(){

    return fetch(
        "http://www.condast.com:8080/churuatas/organisation/find-all",
        {   method: 'GET',
            headers: new Headers(
               {"Content-Type": "text/plain"}
            )
         }
       ).then((response) => { 
        return response.text().then((data) => { 
            //console.log(data)
            return showOrganisationDetails(data); })
            
        .catch(err => console.log(err))
        
        })}