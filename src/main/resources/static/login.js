getKaptcha()

function getKaptcha(){
  var xhr=new XMLHttpRequest()
  xhr.open('GET',host+'/v1/kaptcha/image')
  xhr.responseType='blob'
  xhr.onload=()=>{
    if(xhr.readyState===4){
      if(xhr.status===200){
        var imageURL=URL.createObjectURL(xhr.response)
        $('#login-kaptcha-img').attr('src',imageURL)
      }
    }
  }
  xhr.send()
}

function checkLoginForm(){
  let items=[]
  if($('#login-username').val()===''){
    items.push('username')
    $('#login-username').removeClass('is-valid')
    $('#login-username').addClass('is-invalid')
  }else{
    $('#login-username').removeClass('is-invalid')
    $('#login-username').addClass('is-valid')
  }
  if($('#login-password').val()===''){
    items.push('password')
    $('#login-password').removeClass('is-valid')
    $('#login-password').addClass('is-invalid')
  }else{
    $('#login-password').removeClass('is-invalid')
    $('#login-password').addClass('is-valid')
  }
  if($('#login-kaptcha').val()===''){
    items.push('kaptcha')
    $('#login-kaptcha').removeClass('is-valid')
    $('#login-kaptcha').addClass('is-invalid')
  }else{
    $('#login-kaptcha').removeClass('is-invalid')
    $('#login-kaptcha').addClass('is-valid')
  }
  if(items.length===0){
    if(/^[a-zA-Z\d]{5}$/g.test($('#login-kaptcha').val())){
      return true
    }else{
      $('#login-kaptcha').removeClass('is-valid')
      $('#login-kaptcha').addClass('is-invalid')
      setAlert('The kaptcha must be 5 characters! Only letters and numbers are allowed!')
      return false
    }
  }else {
    let alertContent='Please input the '
    if(items.length===1)
      alertContent+=items[0]+'!'
    else if(items.length===2)
      alertContent+=items[1]+' and '+items[0]+'!'
    else if(items.length===3)
      alertContent+=items[2]+', '+items[1]+' and '+items[0]+'!'
      setAlert(alertContent)
    return false
  }
}

function login(){
  if(checkLoginForm()){
    $('fieldset').attr('disabled',true)
    $.ajax({
      url:host+'/v1/login',
      type:'POST',
      headers:{
          'Content-Type':'application/json'
      },
      data:JSON.stringify({
          username:$('#login-username').val(),
          password:$('#login-password').val()
      }),
      success:(res)=>{
        $('fieldset').attr('disabled',false)
        if(res.success){
          window.location.href='/index.html'
        }else{
          $('#login-username').removeClass('is-valid')
          $('#login-username').addClass('is-invalid')
          $('#login-password').removeClass('is-valid')
          $('#login-password').addClass('is-invalid')
          $('#login-kaptcha').val('')
          $('#login-kaptcha').removeClass('is-invalid')
          $('#login-kaptcha').removeClass('is-valid')
          getKaptcha()
          setAlert(res.message)
        }
      },
      error: function(jqXHR, textStatus, errorThrown) {
        if (jqXHR.status === 400) {
          // Bad Request
          console.log('Bad Request');
        } else {
          // Other error occurred
          console.log('Error:', errorThrown);
        }
        $('fieldset').attr('disabled',false)
        setAlert('Oops! Something wroung! Try again later!')
      }
    })
  }
}