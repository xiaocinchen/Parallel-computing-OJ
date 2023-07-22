/**
 * Step
 */
function toInformation() {
    $('#verify-code-invalid').html('')
    $('#verify-code').removeClass('is-valid')
    $('#verify-code').removeClass('is-invalid')
    $('#verify-code').val('')
    stepChange(1)
}

function toVerify() {
    stepChange(2)
}

function toFinish() {
    stepChange(3)
}


/**
 * Information
 */
function checkInformationFormUsername() {
    if ($('#information-username').val() === '') {
        setInvalid('#information-username', '#information-username-invalid', 'Please input your username!')
        return false
    }
    if (/^\w{6,20}$/g.test($('#information-username').val())) {
        setValid('#information-username')
        return true
    } else {
        setInvalid('#information-username', '#information-username-invalid', 'Username must be 6-20 characters! Only letters, numbers and underscore are allowed!')
        return false
    }
}

function checkInformationFormEmail() {
    if ($('#information-email').val() === '') {
        setInvalid('#information-email', '#information-email-invalid', 'Please input your email!')
        return false
    }
    if (/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g.test($('#information-email').val())) {
        setValid('#information-email')
        return true
    } else {
        setInvalid('#information-email', '#information-email-invalid', 'Your email format is incorrect!')
        return false
    }
}

function checkInformationForm() {
    let username = checkInformationFormUsername()
    let email = checkInformationFormEmail()
    return username && email
}

function information() {
    if (checkInformationForm()) {
        $('fieldset').attr('disabled', true)
        $.ajax({
            url: host + '/v1/password/forget',
            type: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify({
                username: $('#information-username').val(),
                email: $('#information-email').val()
            }),
            success: (res) => {
                $('fieldset').attr('disabled', false)
                if (res.success) {
                    toVerify()
                } else {
                    if (res.code === -1 || res.code===-2) {
                        setAlert("The username does not match the email!")
                        setInvalid('#information-username', '#information-username-invalid', '')
                        setInvalid('#information-email', '#information-email-invalid', '')
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.status === 400) {
                    // Bad Request
                    console.log('Bad Request');
                } else {
                    // Other error occurred
                    console.log('Error:', errorThrown);
                }
                $('fieldset').attr('disabled', false)
                setAlert('Oops! Something wroung! Try again later!')
            }
        })
    }

}


/**
 * Verify
 */
let incorrectCode = ''
function checkVerifyFormCode(){
    if ($('#verify-code').val() === '') {
        setInvalid('#verify-code', '#verify-code-invalid', 'Please input the verification code from youe email!')
        return false
    }
    if (/^[a-zA-Z\d]{5}$/g.test($('#verify-code').val())) {
        if ($('#verify-code').val() === incorrectCode) {
            setInvalid('#verify-code', '#verify-code-invalid', 'Incorrect verification code!')
            return false
        } else {
            setValid('#verify-code')
            return true
        }
    } else {
        setInvalid('#verify-code', '#verify-code-invalid', 'The verification code must be 5 characters! Only letters and numbers are allowed!')
        return false
    }
}

let oldPassword=''
function checkVerifyFormNewPassword(){
    if ($('#verify-new-password').val() === '') {
        setInvalid('#verify-new-password', '#verify-new-password-invalid', 'Please input your password!')
        return false
    }
    if (/^[a-zA-Z\d]{8,20}$/g.test($('#verify-new-password').val())) {
        if ($('#verify-new-password').val() === oldPassword) {
            setInvalid('#verify-new-password', '#verify-new-password-invalid', 'New password must be different from the original one!')
            return false
        } else {
            setValid('#verify-new-password')
            return true
        }
    } else {
        setInvalid('#verify-new-password', '#verify-new-password-invalid', 'Password must be 8-20 characters. Only letters and numbers are allowed!')
        return false
    }
}

function checkVerifyFormConfirmPassword() {
    if ($('#verify-confirm-password').val() === '') {
        setInvalid('#verify-confirm-password', '#verify-confirm-password-invalid', 'Please confirm your new password!')
        return false
    }
    if ($('#verify-confirm-password').val() === $('#verify-new-password').val()) {
        setValid('#verify-confirm-password')
        return true
    } else {
        setInvalid('#verify-confirm-password', '#verify-confirm-password-invalid', 'Passwords must be same!')
        return false
    }
}

function checkVerifyForm() {
    let code=checkVerifyFormCode()
    let newPassword=checkVerifyFormNewPassword()
    let confirmPassword=checkVerifyFormConfirmPassword()
    return code && newPassword && confirmPassword
}

function verify(){
    if(checkVerifyForm()){
        $('fieldset').attr('disabled', true)
        $.ajax({
            url: host + '/v1/password/modify',
            type: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify({
                kaptchaCode: $('#verify-code').val(),
                username: $('#information-username').val(),
                password: $('#verify-new-password').val()
            }),
            success: (res) => {
                $('fieldset').attr('disabled', false)
                if (res.success) {
                    toFinish()
                } else {
                    if (res.code === -1) {
                        setAlert('Oops! Something wroung! Try again later!')
                        toInformation()
                    } else if (res.code === -2) {
                        setAlert("New password must be different from the original one!")
                        oldPassword = $('#verify-new-password').val()
                        checkVerifyFormNewPassword()
                    } else if (res.code === -3) {
                        setAlert("Please input correct verification code!")
                        incorrectCode = $('#verify-code').val()
                        checkVerifyFormCode()
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.status === 400) {
                    // Bad Request
                    console.log('Bad Request');
                } else {
                    // Other error occurred
                    console.log('Error:', errorThrown);
                }
                $('fieldset').attr('disabled', false)
                setAlert('Oops! Something wroung! Try again later!')
            }
        })
    }

}
