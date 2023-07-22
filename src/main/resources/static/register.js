/**
 * Step
 */
function toRegister() {
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
 * Register
 */
let existUsername = ''
function checkRegisterFormUsername() {
    if ($('#register-username').val() === '') {
        setInvalid('#register-username', '#register-username-invalid', 'Please input your username!')
        return false
    }
    if (/^\w{6,20}$/g.test($('#register-username').val())) {
        if ($('#register-username').val() === existUsername) {
            setInvalid('#register-username', '#register-username-invalid', 'Username already exists!')
            return false
        } else {
            setValid('#register-username')
            return true
        }
    } else {
        setInvalid('#register-username', '#register-username-invalid', 'Username must be 6-20 characters! Only letters, numbers and underscore are allowed!')
        return false
    }
}

let existEmail = ''
function checkRegisterFormEmail() {
    if ($('#register-email').val() === '') {
        setInvalid('#register-email', '#register-email-invalid', 'Please input your email!')
        return false
    }
    if (/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g.test($('#register-email').val())) {
        if ($('#register-email').val() === existEmail) {
            setInvalid('#register-email', '#register-email-invalid', 'Email already exists!')
            return false
        } else {
            setValid('#register-email')
            return true
        }
    } else {
        setInvalid('#register-email', '#register-email-invalid', 'Your email format is incorrect!')
        return false
    }
}

function checkRegisterFormFirstName() {
    if ($('#register-first-name').val() === '') {
        setInvalid('#register-first-name', '#register-first-name-invalid', 'Please input your first name!')
        return false
    }
    if (/^[a-zA-Z]{1,12}$/g.test($('#register-first-name').val())) {
        setValid('#register-first-name')
        return true
    } else {
        setInvalid('#register-first-name', '#register-first-name-invalid', 'Your first name can\'t be longer than 12 characters! Only letters are allowed!')
        return false
    }
}

function checkRegisterFormLastName() {
    if ($('#register-last-name').val() === '') {
        setInvalid('#register-last-name', '#register-last-name-invalid', 'Please input your last name!')
        return false
    }
    if (/^[a-zA-Z]{1,12}$/g.test($('#register-last-name').val())) {
        setValid('#register-last-name')
        return true
    } else {
        setInvalid('#register-last-name', '#register-last-name-invalid', 'Your last name can\'t be longer than 12 characters! Only letters are allowed!')
        return false
    }
}

function checkRegisterFormPassword() {
    if ($('#register-password').val() === '') {
        setInvalid('#register-password', '#register-password-invalid', 'Please input your password!')
        return false
    }
    if (/^[a-zA-Z\d]{8,20}$/g.test($('#register-password').val())) {
        setValid('#register-password')
        return true
    } else {
        setInvalid('#register-password', '#register-password-invalid', 'Password must be 8-20 characters. Only letters and numbers are allowed!')
        return false
    }
}

function checkRegisterFormConfirmPassword() {
    if ($('#register-confirm-password').val() === '') {
        setInvalid('#register-confirm-password', '#register-confirm-password-invalid', 'Please confirm your password!')
        return false
    }
    if ($('#register-confirm-password').val() === $('#register-password').val()) {
        setValid('#register-confirm-password')
        return true
    } else {
        setInvalid('#register-confirm-password', '#register-confirm-password-invalid', 'Passwords must be same!')
        return false
    }
}

function checkRegisterForm() {
    let username = checkRegisterFormUsername()
    let email = checkRegisterFormEmail()
    let firstName = checkRegisterFormFirstName()
    let lastName = checkRegisterFormLastName()
    let password = checkRegisterFormPassword()
    let confirmPassword = checkRegisterFormConfirmPassword()
    return username && email && firstName && lastName && password && confirmPassword
}


function register() {
    if (checkRegisterForm()) {
        $('fieldset').attr('disabled', true)
        $.ajax({
            url: host + '/v1/register/send',
            type: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify({
                username: $('#register-username').val(),
                email: $('#register-email').val(),
                firstName: $('#register-first-name').val(),
                lastName: $('#register-last-name').val(),
                gender: $('#register-gender').find(":selected").val(),
                password: $('#register-password').val()
            }),
            success: (res) => {
                $('fieldset').attr('disabled', false)
                if (res.success) {
                    toVerify()
                } else {
                    if (res.code === -1) {
                        setAlert("Please select a correct gender!")
                    } else if (res.code === -2) {
                        setAlert("Username already exists! Please input a new username!")
                        existUsername = $('#register-username').val()
                        checkRegisterFormUsername();
                    } else if (res.code === -3) {
                        setAlert("Email already exists! Please input a new email!")
                        existEmail = $('#register-email').val()
                        checkRegisterFormEmail();
                    } else if (res.code === -4) {
                        setAlert('Oops! Something wroung! Try again later!')
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
function checkVerifyForm() {
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

function verify() {
    if (checkVerifyForm()) {
        $('fieldset').attr('disabled', true)
        $.ajax({
            url: host + '/v1/register/verify',
            type: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify({
                username: $('#register-username').val(),
                type: 'REGISTER',
                code: $('#verify-code').val()
            }),
            success: (res) => {
                $('fieldset').attr('disabled', false)
                if (res.success) {
                    toFinish()
                } else {
                    if (res.code === -1) {
                        setAlert("Verification code has expired!")
                        toRegister()
                    } else if (res.code === -2) {
                        setAlert("Please input correct verification code!")
                        incorrectCode = $('#verify-code').val()
                        checkVerifyForm()
                    } else if (res.code === -3) {
                        setAlert('Oops! Something wroung! Try again later!')
                        toRegister()
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