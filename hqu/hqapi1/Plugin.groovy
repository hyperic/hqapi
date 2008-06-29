import org.hyperic.hq.hqu.rendit.HQUPlugin

// Import controllers for deployment time syntax checking.
import UserController

class Plugin extends HQUPlugin {
    Plugin() {
        addAdminView(true, '/api/index.hqu', 'HQ Web Services Api')
    }
}

