import React, { lazy } from 'react';
const CreateIcon: React.FC<{name:string|undefined}> = (prop) => {
    return (prop.name?
        <React.Suspense fallback={<></>}>
            {React.createElement(lazy(() => import('@ant-design/icons').then(module => {
                return { default: module[prop.name] };
            })), {})}
        </React.Suspense>:<></>
    )
}
export default CreateIcon;