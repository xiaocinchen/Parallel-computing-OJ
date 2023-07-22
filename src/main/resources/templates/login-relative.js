// let host='http://127.0.0.1:8081'
let host='http://43.134.185.77:8081'

/**
 * Step
 */
function setIndicator(index) {
    if (index == 1) {
        $('.steps-indicator').css({ 'width': '150px' })
        $('.step-1').addClass('step-done')
        $('.step-2').removeClass('step-done')
        $('.step-3').removeClass('step-done')
    } else if (index == 2) {
        $('.steps-indicator').css({ 'width': '300px' })
        $('.step-1').addClass('step-done')
        $('.step-2').addClass('step-done')
        $('.step-3').removeClass('step-done')
    } else if (index == 3) {
        $('.steps-indicator').css({ 'width': '450px' })
        $('.step-1').addClass('step-done')
        $('.step-2').addClass('step-done')
        $('.step-3').addClass('step-done')
    }
}

function setFrom(index) {
    const carousel = new bootstrap.Carousel('.carousel-form')
    carousel.to(index - 1)
}

function stepChange(step) {
    if(step!==null && step>0 &&step<4){
        setIndicator(step)
        setFrom(step)
    }
}


/**
 * Alert
 */
function setAlert(content) {
    $('.alert div').html(content)
    $('.alert').addClass('show')
    window.setTimeout(() => {
        $('.alert').removeClass('show')
    }, 3000)
}


/**
 * Validation
 */
function setValid(ele) {
    $(ele).removeClass('is-invalid')
    $(ele).addClass('is-valid')
}

function setInvalid(ele, eleInvalid, content) {
    $(eleInvalid).html(content)
    $(ele).removeClass('is-valid')
    $(ele).addClass('is-invalid')
}


/**
 * Finish
 */
function finish(){
    window.location.href='/view/login'
}